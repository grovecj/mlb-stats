# Digital Ocean API Token
variable "do_token" {
  description = "Digital Ocean API token"
  type        = string
  sensitive   = true
}

# Application Configuration
variable "app_name" {
  description = "Name of the application"
  type        = string
  default     = "mlb-stats"
}

variable "region" {
  description = "Digital Ocean region"
  type        = string
  default     = "nyc"
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = list(string)
  default     = ["mlb-stats", "production"]
}

# GitHub Configuration
variable "github_token" {
  description = "GitHub personal access token with repo permissions"
  type        = string
  sensitive   = true
}

variable "github_repo" {
  description = "GitHub repository (owner/repo format)"
  type        = string
}

variable "github_branch" {
  description = "GitHub branch to deploy"
  type        = string
  default     = "main"
}

variable "required_approvals" {
  description = "Number of required PR approvals before merging"
  type        = number
  default     = 1
}

# App Platform Configuration
variable "instance_size" {
  description = "App Platform instance size"
  type        = string
  default     = "basic-xxs" # $5/month, 512MB RAM
}

variable "instance_count" {
  description = "Number of instances"
  type        = number
  default     = 1
}

# Database Configuration
variable "database_size" {
  description = "Database instance size"
  type        = string
  default     = "db-s-1vcpu-1gb" # $15/month
}

# Google OAuth Credentials
variable "google_client_id" {
  description = "Google OAuth Client ID"
  type        = string
  sensitive   = true
}

variable "google_client_secret" {
  description = "Google OAuth Client Secret"
  type        = string
  sensitive   = true
}

# Application Secrets
variable "ingestion_api_key" {
  description = "API key for ingestion endpoints"
  type        = string
  sensitive   = true
}

# Owner Configuration
variable "owner_email" {
  description = "Email address of the application owner (gets OWNER role)"
  type        = string
}

# Google Analytics 4
variable "ga_measurement_id" {
  description = "Google Analytics 4 Measurement ID"
}
  
# Custom Domain
variable "custom_domain" {
  description = "Custom domain for the application (e.g., stats.cartergrove.me)"
  type        = string
  default     = ""
}

# Datadog Monitoring
variable "datadog_enabled" {
  description = "Enable Datadog metrics export"
  type        = bool
  default     = false
}

variable "datadog_api_key" {
  description = "Datadog API key"
  type        = string
  sensitive   = true
  default     = ""
}

variable "datadog_app_key" {
  description = "Datadog Application key"
  type        = string
  sensitive   = true
  default     = ""
}
