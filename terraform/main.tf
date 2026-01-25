terraform {
  required_version = ">= 1.0"

  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "~> 2.34"
    }
  }
}

provider "digitalocean" {
  token = var.do_token
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
    }
  }
}
