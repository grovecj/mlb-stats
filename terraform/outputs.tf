output "app_url" {
  description = "The URL of the deployed application"
  value       = digitalocean_app.mlb_stats.live_url
}

output "app_id" {
  description = "The ID of the App Platform application"
  value       = digitalocean_app.mlb_stats.id
}

output "database_host" {
  description = "Database hostname"
  value       = digitalocean_database_cluster.postgres.host
}

output "database_port" {
  description = "Database port"
  value       = digitalocean_database_cluster.postgres.port
}

output "database_name" {
  description = "Database name"
  value       = digitalocean_database_cluster.postgres.database
}

output "database_user" {
  description = "Database username"
  value       = digitalocean_database_cluster.postgres.user
}

output "oauth_redirect_uri" {
  description = "OAuth redirect URI to configure in Google Cloud Console"
  value       = var.custom_domain != "" ? "https://${var.custom_domain}/login/oauth2/code/google" : "${digitalocean_app.mlb_stats.live_url}/login/oauth2/code/google"
}

output "default_hostname" {
  description = "Default DO hostname (use for CNAME record if using custom domain)"
  value       = replace(digitalocean_app.mlb_stats.default_ingress, "https://", "")
}

output "custom_domain_dns" {
  description = "DNS configuration instructions for custom domain"
  value       = var.custom_domain != "" ? "Add CNAME record: ${var.custom_domain} -> ${replace(digitalocean_app.mlb_stats.default_ingress, "https://", "")}" : "No custom domain configured"
}
