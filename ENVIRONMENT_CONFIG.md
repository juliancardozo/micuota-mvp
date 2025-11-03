# 🔧 Environment Configuration Guide

## ⚠️ IMPORTANT: Security Changes Made

This project has been updated to use **environment variables** instead of hardcoded values for better security and flexibility.

## 🚀 Quick Setup

### 1. Copy the environment template
```bash
cp .env.example .env
```

### 2. Edit `.env` with your actual values
```bash
# REQUIRED: Change these for production!
JWT_SECRET=your_super_secure_secret_here
SPRING_DATASOURCE_PASSWORD=your_secure_db_password

# OPTIONAL: MercadoPago configuration (when ready to implement)
MERCADOPAGO_ACCESS_TOKEN=your_access_token
MERCADOPAGO_CLIENT_ID=your_client_id
MERCADOPAGO_CLIENT_SECRET=your_client_secret
```

## 📋 Environment Variables Reference

### 🔐 Critical Security Variables
| Variable | Description | Default | Production Required |
|----------|-------------|---------|-------------------|
| `JWT_SECRET` | JWT signing secret | `ChangeThisSecretKeyForJWTSigning` | ✅ YES - CHANGE THIS! |
| `JWT_EXPIRATION` | Token expiration (ms) | `86400000` (24h) | ⚠️ Recommended |

### 🗄️ Database Variables
| Variable | Description | Default | Production Required |
|----------|-------------|---------|-------------------|
| `SPRING_DATASOURCE_URL` | Database URL | `jdbc:postgresql://localhost:5432/micuota` | ✅ YES |
| `SPRING_DATASOURCE_USERNAME` | DB Username | `postgres` | ✅ YES |
| `SPRING_DATASOURCE_PASSWORD` | DB Password | `root` | ✅ YES - CHANGE THIS! |

### 💳 MercadoPago Variables
| Variable | Description | Default | Status |
|----------|-------------|---------|--------|
| `MERCADOPAGO_ACCESS_TOKEN` | MP Access Token | Empty | 🔄 For future implementation |
| `MERCADOPAGO_CLIENT_ID` | MP Client ID | Empty | 🔄 For future implementation |
| `MERCADOPAGO_CLIENT_SECRET` | MP Client Secret | Empty | 🔄 For future implementation |
| `MERCADOPAGO_SANDBOX` | Use sandbox mode | `true` | 🔄 Set to false in production |

## 🏗️ Environment Profiles

### Development (`SPRING_PROFILES_ACTIVE=dev`)
- **SQL Logging**: Enabled
- **H2 Console**: Available at `/h2-console`
- **Debug Logging**: Enabled for `com.micuota`
- **CORS**: Permissive for local development
- **MercadoPago**: Sandbox mode

### Production (`SPRING_PROFILES_ACTIVE=prod`)
- **SQL Logging**: Disabled
- **Security Headers**: Enabled
- **Error Details**: Hidden
- **Logging**: Info level only
- **CORS**: Restricted to specific domains
- **MercadoPago**: Production mode

## 🔒 Security Best Practices

### ✅ DO:
- Use strong, unique secrets for `JWT_SECRET` (at least 256 bits)
- Set restrictive database passwords
- Use HTTPS in production
- Set `SPRING_PROFILES_ACTIVE=prod` in production
- Keep `.env` file out of version control

### ❌ DON'T:
- Commit `.env` files to Git
- Use default passwords in production
- Expose debug endpoints in production
- Use the same JWT secret across environments

## 🛠️ MercadoPago Integration Status

Currently **MOCK IMPLEMENTATION** - returns fake IDs:
- `createPlan()` → `fake-plan-id-{timestamp}`
- `createSubscription()` → `fake-subscription-id-{timestamp}`

### When ready to implement real API:
1. Get credentials from [MercadoPago Developers](https://www.mercadopago.com/developers/)
2. Set environment variables
3. Uncomment TODO sections in `MercadoPagoService.java`
4. Add MercadoPago SDK dependency to `pom.xml`

## 🐳 Docker Environment Variables

The `docker-compose.yml` has been updated to use environment variables:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/micuota
  JWT_SECRET: ${JWT_SECRET:-ChangeThisSecretKeyForJWTSigning}
  MERCADOPAGO_ACCESS_TOKEN: ${MERCADOPAGO_ACCESS_TOKEN:-}
```

## 🔍 Troubleshooting

### Problem: "JWT_SECRET not found"
**Solution**: Set the `JWT_SECRET` environment variable or copy `.env.example` to `.env`

### Problem: "Database connection failed"
**Solution**: Check database environment variables and ensure PostgreSQL is running

### Problem: "MercadoPago errors"
**Solution**: This is expected - MercadoPago is currently mocked. Set real credentials when ready.

## 📝 Next Steps

1. **Immediate**: Change `JWT_SECRET` for any non-local environment
2. **Before Production**: Set all production database credentials  
3. **When Ready**: Implement real MercadoPago integration
4. **Deploy**: Use `SPRING_PROFILES_ACTIVE=prod` in production

---
📚 **Need help?** Check the main [README.md](./README.md) or [QUICK_START.md](./QUICK_START.md) guides.