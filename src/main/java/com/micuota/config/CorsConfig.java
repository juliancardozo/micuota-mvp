package com.micuota.config;

// This class previously provided a CorsConfigurationSource bean. CORS is now configured
// centrally in SecurityConfig to avoid duplicate bean definitions. The file is kept
// as a non-bean placeholder to preserve history and avoid accidental re-introduction.
public final class CorsConfig {
    private CorsConfig() { }
}
