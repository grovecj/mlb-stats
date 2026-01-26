terraform {
  required_version = ">= 1.0"

  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "~> 2.34"
    }
    github = {
      source  = "integrations/github"
      version = "~> 6.0"
    }
  }
}

provider "digitalocean" {
  token = var.do_token
}

provider "github" {
  token = var.github_token
  owner = local.github_owner
}

locals {
  github_parts = split("/", var.github_repo)
  github_owner = local.github_parts[0]
  github_repo  = local.github_parts[1]
}

# Managed PostgreSQL Database
resource "digitalocean_database_cluster" "postgres" {
  name       = "${var.app_name}-db"
  engine     = "pg"
  version    = "16"
  size       = var.database_size
  region     = var.region
  node_count = 1

  tags = var.tags
}

# Database firewall - only allow App Platform
resource "digitalocean_database_firewall" "postgres_fw" {
  cluster_id = digitalocean_database_cluster.postgres.id

  rule {
    type  = "app"
    value = digitalocean_app.mlb_stats.id
  }
}

# App Platform Application
resource "digitalocean_app" "mlb_stats" {
  spec {
    name   = var.app_name
    region = var.region

    # Custom domain (if configured)
    dynamic "domain" {
      for_each = var.custom_domain != "" ? [var.custom_domain] : []
      content {
        name = domain.value
        type = "PRIMARY"
      }
    }

    # Alert on deployment failure
    alert {
      rule = "DEPLOYMENT_FAILED"
    }

    service {
      name               = "web"
      instance_count     = var.instance_count
      instance_size_slug = var.instance_size
      http_port          = 8080

      github {
        repo           = var.github_repo
        branch         = var.github_branch
        deploy_on_push = true
      }

      dockerfile_path = "Dockerfile"

      health_check {
        http_path             = "/actuator/health"
        initial_delay_seconds = 60
        period_seconds        = 30
        timeout_seconds       = 10
        failure_threshold     = 3
      }

      # Environment variables
      env {
        key   = "DATABASE_URL"
        value = "jdbc:postgresql://${digitalocean_database_cluster.postgres.host}:${digitalocean_database_cluster.postgres.port}/${digitalocean_database_cluster.postgres.database}?sslmode=require"
        type  = "GENERAL"
      }

      env {
        key   = "DATABASE_USERNAME"
        value = digitalocean_database_cluster.postgres.user
        type  = "GENERAL"
      }

      env {
        key   = "DATABASE_PASSWORD"
        value = digitalocean_database_cluster.postgres.password
        type  = "SECRET"
      }

      env {
        key   = "GOOGLE_CLIENT_ID"
        value = var.google_client_id
        type  = "SECRET"
      }

      env {
        key   = "GOOGLE_CLIENT_SECRET"
        value = var.google_client_secret
        type  = "SECRET"
      }

      env {
        key   = "INGESTION_API_KEY"
        value = var.ingestion_api_key
        type  = "SECRET"
      }

      env {
        key   = "JAVA_OPTS"
        value = "-Xmx512m"
        type  = "GENERAL"
      }

      env {
        key   = "FRONTEND_URL"
        value = "" # Empty means same origin
        type  = "GENERAL"
      }

      env {
        key   = "OWNER_EMAIL"
        value = var.owner_email
        type  = "GENERAL"
      }

      env {
        key   = "VITE_GA_MEASUREMENT_ID"
        value = var.ga_measurement_id
        scope = "BUILD_TIME"
        type  = "GENERAL"
      }

      env {
        key   = "DATADOG_ENABLED"
        value = var.datadog_enabled ? "true" : "false"
        type  = "GENERAL"
      }

      env {
        key   = "DATADOG_API_KEY"
        value = var.datadog_api_key
        type  = "SECRET"
      }

      env {
        key   = "DATADOG_APP_KEY"
        value = var.datadog_app_key
        type  = "SECRET"
      }

      env {
        key   = "ENVIRONMENT"
        value = "production"
        type  = "GENERAL"
      }
    }
  }
}

# DNS Record for custom subdomain (if domain is managed in DO)
resource "digitalocean_record" "app_cname" {
  count  = var.custom_domain != "" ? 1 : 0
  domain = join(".", slice(split(".", var.custom_domain), 1, length(split(".", var.custom_domain))))
  type   = "CNAME"
  name   = split(".", var.custom_domain)[0]
  value  = "${replace(digitalocean_app.mlb_stats.default_ingress, "https://", "")}."
  ttl    = 3600
}

# GitHub Repository
resource "github_repository" "mlb_stats" {
  name         = local.github_repo
  description  = "Vibe Coded MLB Stats Webapp"
  homepage_url = var.custom_domain != "" ? "https://${var.custom_domain}" : null
  visibility   = "public"

  has_issues   = true
  has_projects = true
  has_wiki     = false

  delete_branch_on_merge = true

  # Enable Dependabot security alerts
  vulnerability_alerts = true
}

# Enable Dependabot security updates (auto-PRs for vulnerabilities)
resource "github_repository_dependabot_security_updates" "mlb_stats" {
  repository = github_repository.mlb_stats.name
  enabled    = true
}

# GitHub Branch Protection
resource "github_branch_protection" "main" {
  repository_id = github_repository.mlb_stats.name
  pattern       = var.github_branch

  required_status_checks {
    strict = true # Require branch to be up to date before merging
    contexts = [
      "Frontend Build & Test",
      "Backend Build & Test"
    ]
  }

  required_pull_request_reviews {
    required_approving_review_count = var.required_approvals
    dismiss_stale_reviews           = true
    require_code_owner_reviews      = true
  }

  # Prevent force pushes
  allows_force_pushes = false

  # Prevent branch deletion
  allows_deletions = false

  # Require conversation resolution before merging
  require_conversation_resolution = true
}
