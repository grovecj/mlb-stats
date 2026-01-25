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
  value       = "${digitalocean_app.mlb_stats.live_url}/login/oauth2/code/google"
}
