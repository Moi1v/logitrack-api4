# LogiTrack Distribution API

API REST empresarial para la gestión de distribución de productos a pequeños comercios. Sistema desarrollado con Jakarta EE 10, JAX-RS, JPA/Hibernate y PostgreSQL.

## 📋 Tabla de Contenidos

- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Endpoints de la API](#endpoints-de-la-api)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Arquitectura y Funcionalidad](#arquitectura-y-funcionalidad)
- [Reglas de Negocio](#reglas-de-negocio)

## 🔧 Requisitos Previos

- Java 21
- Maven 3.8+
- Docker
- WildFly 31+ (Jakarta EE 10)
- PostgreSQL 15+ (via Docker)

## 🚀 Instalación y Configuración

### 1. Configurar la Base de Datos con Docker

```bash
docker run --name logitrackapi \
  -e POSTGRES_PASSWORD=admin123 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=logitrackapi \
  -p 5433:5432 \
  -d postgres
```

### 2. Configurar Variables de Entorno

Editar `src/main/resources/config/.env`:

```properties
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://localhost:5433/logitrackapi
DB_USER=postgres
DB_PASSWORD=admin123

HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
HIBERNATE_DDL=update
HIBERNATE_SHOW_SQL=true
HIBERNATE_FORMAT_SQL=true
```

### 3. Compilar y Desplegar

```bash
# Compilar el proyecto
mvn clean package

# Desplegar en WildFly
cp target/logitrack-api.war $WILDFLY_HOME/standalone/deployments/
```

### 4. Verificar el Despliegue

La API estará disponible en: `http://localhost:8080/logitrack-api/api/v1`

## 📁 Estructura del Proyecto

```
logitrack-api/
├── src/main/java/com/mcabrera/logitrackapi/
│   ├── config/              # Configuración JPA y variables de entorno
│   │   ├── EnvListener.java
│   │   ├── JpaProducer.java
│   │   ├── CorsFilter.java
│   │   └── OptionsRequestFilter.java
│   ├── controllers/         # Endpoints REST (JAX-RS)
│   │   ├── CustomerController.java
│   │   ├── ProductController.java
│   │   ├── OrderController.java
│   │   └── PaymentController.java
│   ├── models/             # Entidades JPA
│   │   ├── Customer.java
│   │   ├── Product.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── Payment.java
│   ├── repositories/       # Capa de acceso a datos
│   │   ├── BaseRepository.java
│   │   ├── CustomerRepository.java
│   │   ├── ProductRepository.java
│   │   ├── OrderRepository.java
│   │   ├── OrderItemRepository.java
│   │   └── PaymentRepository.java
│   ├── services/           # Lógica de negocio
│   │   ├── CustomerService.java
│   │   ├── ProductService.java
│   │   ├── OrderService.java
│   │   └── PaymentService.java
│   └── dtos/               # Data Transfer Objects
│       ├── CreateOrderDto.java
│       ├── OrderResponseDto.java
│       ├── OrderItemDto.java
│       ├── PaymentDto.java
│       └── ProductStatsDto.java
└── src/main/resources/
    ├── META-INF/
    │   ├── persistence.xml
    │   └── beans.xml
    └── config/
        └── .env
```

## 🌐 Endpoints de la API

Base URL: `http://localhost:8080/logitrack-api/api/v1`

### 👥 Clientes (`/customers`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/customers` | Listar todos los clientes |
| GET | `/customers/active` | Listar clientes activos |
| GET | `/customers/{id}` | Obtener cliente por ID |
| GET | `/customers/tax-id/{taxId}` | Buscar cliente por NIT |
| POST | `/customers` | Crear nuevo cliente |
| PUT | `/customers/{id}` | Actualizar cliente |
| PATCH | `/customers/{id}/activate` | Activar cliente |
| PATCH | `/customers/{id}/deactivate` | Desactivar cliente |
| DELETE | `/customers/{id}` | Eliminar cliente |

### 📦 Productos (`/products`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/products` | Listar todos los productos |
| GET | `/products/active` | Listar productos activos |
| GET | `/products/{id}` | Obtener producto por ID |
| GET | `/products/category/{category}` | Productos por categoría |
| GET | `/products/top-selling?limit=10` | Productos más vendidos |
| GET | `/products/top-selling/category/{category}?limit=10` | Top ventas por categoría |
| GET | `/products/{id}/stats` | Estadísticas de un producto |
| POST | `/products` | Crear nuevo producto |
| PUT | `/products/{id}` | Actualizar producto |
| PATCH | `/products/{id}/activate` | Activar producto |
| PATCH | `/products/{id}/deactivate` | Desactivar producto |
| DELETE | `/products/{id}` | Eliminar producto |

### 🛒 Órdenes (`/orders`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/orders` | Listar todas las órdenes |
| GET | `/orders/{id}` | Obtener orden por ID |
| GET | `/orders/customer/{customerId}` | Órdenes de un cliente |
| GET | `/orders/status/{status}` | Órdenes por estado |
| GET | `/orders/incomplete` | Órdenes incompletas |
| GET | `/orders/customer/{customerId}/debt` | Deuda total del cliente |
| POST | `/orders` | Crear nueva orden |
| PUT | `/orders/{id}/status` | Actualizar estado de orden |

### 💰 Pagos (`/payments`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/payments` | Listar todos los pagos |
| GET | `/payments/{id}` | Obtener pago por ID |
| GET | `/payments/order/{orderId}` | Pagos de una orden |
| GET | `/payments/order/{orderId}/total-paid` | Total pagado de una orden |
| POST | `/payments` | Registrar nuevo pago |

## 📝 Ejemplos de Uso

### Crear un Cliente

```bash
POST http://localhost:8080/logitrack-api/api/v1/customers
Content-Type: application/json

{
  "fullName": "Tienda El Éxito",
  "taxId": "12345678-9",
  "email": "tienda@exito.com",
  "address": "Zona 10, Ciudad de Guatemala",
  "active": true
}
```

### Crear un Producto

```bash
POST http://localhost:8080/logitrack-api/api/v1/products
Content-Type: application/json

{
  "name": "Laptop HP Pavilion",
  "description": "Laptop para uso empresarial",
  "price": 7500.00,
  "category": "Electronics",
  "active": true
}
```

### Crear una Orden

```bash
POST http://localhost:8080/logitrack-api/api/v1/orders
Content-Type: application/json

{
  "customerId": 4,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 5
    }
  ]
}
```

**Respuesta:**

```json
{
  "orderId": 1,
  "customerId": 4,
  "customerName": "Moisés Cabrera",
  "orderDate": "2025-11-21T04:28:01.731781",
  "status": "Pending",
  "totalAmount": 16750.00,
  "items": [
    {
      "productId": 1,
      "productName": "Laptop HP Pavilion",
      "quantity": 2,
      "unitPrice": 7500.00,
      "subtotal": 15000.00
    },
    {
      "productId": 2,
      "productName": "Teclado Mecánico",
      "quantity": 5,
      "unitPrice": 350.00,
      "subtotal": 1750.00
    }
  ],
  "paidAmount": 0.00,
  "pendingAmount": 16750.00
}
```

### Registrar un Pago

```bash
POST http://localhost:8080/logitrack-api/api/v1/payments
Content-Type: application/json

{
  "orderId": 1,
  "amount": 10000.00,
  "method": "Transfer"
}
```

### Actualizar Estado de Orden

```bash
PUT http://localhost:8080/logitrack-api/api/v1/orders/1/status
Content-Type: application/json

{
  "status": "Completed"
}
```

### Consultar Productos Más Vendidos

```bash
GET http://localhost:8080/logitrack-api/api/v1/products/top-selling?limit=5
```

**Respuesta:**

```json
[
  {
    "productId": 1,
    "name": "Laptop HP Pavilion",
    "category": "Electronics",
    "totalQuantitySold": 150,
    "totalRevenue": 1125000.00,
    "orderCount": 45
  },
  {
    "productId": 2,
    "name": "Teclado Mecánico",
    "category": "Accessories",
    "totalQuantitySold": 320,
    "totalRevenue": 112000.00,
    "orderCount": 89
  }
]
```

### Consultar Deuda de un Cliente

```bash
GET http://localhost:8080/logitrack-api/api/v1/orders/customer/4/debt
```

**Respuesta:**

```json
{
  "customerId": 4,
  "totalDebt": 6750.00
}
```

## 🏗️ Arquitectura y Funcionalidad

### Patrón de Arquitectura: Capas (Layered Architecture)

El proyecto implementa una arquitectura en capas que separa responsabilidades:

#### 1. **Capa de Presentación (Controllers)**
- **Responsabilidad**: Exponer endpoints REST y manejar solicitudes HTTP
- **Tecnología**: JAX-RS (Jakarta RESTful Web Services)
- **Funcionalidad**:
    - Validación de entrada de datos
    - Manejo de códigos de estado HTTP
    - Serialización/deserialización JSON
    - Control de errores con mensajes descriptivos

**Ejemplo:**
```java
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderController {
    @POST
    public Response createOrder(CreateOrderDto orderDto) {
        // Validaciones
        // Delegación a la capa de servicio
        // Retorno de respuesta HTTP
    }
}
```

#### 2. **Capa de Servicio (Services)**
- **Responsabilidad**: Lógica de negocio y orquestación
- **Funcionalidad**:
    - Validación de reglas de negocio
    - Coordinación entre repositorios
    - Transformación de datos (Entity ↔ DTO)
    - Cálculos y procesamiento

**Ejemplo de Lógica Implementada:**
```java
@ApplicationScoped
public class OrderService {
    public Optional<Order> createOrder(CreateOrderDto orderDto) {
        // 1. Validar que el cliente esté activo
        // 2. Validar que los productos existan y estén activos
        // 3. Crear la orden con sus items
        // 4. Calcular totales automáticamente
        // 5. Persistir en base de datos
    }
}
```

#### 3. **Capa de Repositorio (Repositories)**
- **Responsabilidad**: Acceso y manipulación de datos
- **Patrón**: Repository Pattern con Generic Base
- **Funcionalidad**:
    - Operaciones CRUD genéricas (`BaseRepository`)
    - Consultas específicas por entidad
    - Manejo de transacciones

**Implementación del BaseRepository:**
```java
public abstract class BaseRepository<T, ID> {
    @Transactional
    public Optional<T> save(T entity) {
        // Detección automática: nuevo vs existente
        // Uso de merge() para ambos casos
        // Manejo de transacciones RESOURCE_LOCAL
        // Flush y commit explícitos
    }
    
    @Transactional
    public void delete(T entity) {
        // Validación de contexto de persistencia
        // Merge si es necesario
        // Eliminación y commit
    }
}
```

#### 4. **Capa de Persistencia (Models)**
- **Responsabilidad**: Mapeo objeto-relacional
- **Tecnología**: JPA/Hibernate
- **Funcionalidad**:
    - Definición de entidades y relaciones
    - Hooks de ciclo de vida (`@PrePersist`, `@PreUpdate`)
    - Cálculos automáticos (totales, subtotales)

**Ejemplo de Cálculo Automático:**
```java
@Entity
public class Order {
    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        this.totalAmount = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### Funcionalidades Clave Implementadas

#### ✅ **1. Gestión de Transacciones**
- Transacciones RESOURCE_LOCAL (no JTA)
- Manejo manual de `begin()`, `commit()`, `rollback()`
- Uso de `@Transactional` para demarcación
- EntityManager de ciclo `@RequestScoped`

#### ✅ **2. Validaciones Multinivel**

**Nivel Controller:**
- Datos requeridos (nulls, vacíos)
- Formato de datos

**Nivel Service:**
- Reglas de negocio complejas
- Estado de cliente activo
- Productos disponibles
- Montos de pago válidos

**Nivel Repository:**
- Unicidad de datos (NIT, email, nombre de producto)
- Integridad referencial

#### ✅ **3. Consultas Avanzadas**

**JPQL con Agregaciones:**
```java
public List<ProductStatsDto> findTopSellingProducts(int limit) {
    String jpql = "SELECT new com.mcabrera.logitrackapi.dtos.ProductStatsDto(" +
        "p.productId, p.name, p.category, " +
        "SUM(oi.quantity), SUM(oi.subtotal), COUNT(DISTINCT o.orderId)) " +
        "FROM OrderItem oi " +
        "JOIN oi.product p JOIN oi.order o " +
        "WHERE o.status != 'Cancelled' " +
        "GROUP BY p.productId, p.name, p.category " +
        "ORDER BY SUM(oi.quantity) DESC";
}
```

#### ✅ **4. Manejo de Relaciones**
- `@OneToMany` con cascade y orphanRemoval
- `@ManyToOne` con FetchType.LAZY
- Bidireccionalidad controlada
- Helper methods para mantener consistencia

```java
public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);  // Mantener bidireccionalidad
}
```

#### ✅ **5. DTOs y Transformaciones**
- Separación entre modelo de negocio y transferencia
- Evitar lazy loading exceptions
- Inclusión de datos calculados (paidAmount, pendingAmount)
- Proyecciones personalizadas

#### ✅ **6. CORS y Métodos HTTP**
- Filtros para permitir todos los métodos (GET, POST, PUT, DELETE, PATCH)
- Manejo de preflight requests (OPTIONS)
- Headers de seguridad configurados

#### ✅ **7. Inyección de Dependencias**
- CDI (Contexts and Dependency Injection)
- Scopes apropiados (`@ApplicationScoped`, `@RequestScoped`)
- Producer methods para EntityManager y EntityManagerFactory

#### ✅ **8. Configuración Flexible**
- Variables de entorno desde archivo `.env`
- Carga al inicio de la aplicación (`@WebListener`)
- Prioridad: System Properties > Environment Variables > .env

## ⚖️ Reglas de Negocio

### Clientes
- ✅ NIT y email deben ser únicos
- ✅ Cliente inactivo no puede crear nuevas órdenes
- ✅ Validación de formato de datos

### Productos
- ✅ Nombre debe ser único
- ✅ Precio debe ser mayor a cero
- ✅ Solo productos activos pueden agregarse a órdenes

### Órdenes
- ✅ Estado inicial siempre es "Pending"
- ✅ Total se calcula automáticamente desde los items
- ✅ No se pueden crear órdenes sin cliente válido
- ✅ No se pueden agregar productos inactivos

### Pagos
- ✅ Monto no puede exceder el saldo pendiente
- ✅ Monto debe ser mayor a cero
- ✅ Métodos válidos: Cash, Card, Transfer
- ✅ Actualización automática de estado a "Completed" cuando se paga el total
- ✅ Registro de fecha automático

### Estados de Orden
- `Pending`: Orden creada, sin procesar
- `Processing`: En proceso de preparación
- `Completed`: Orden completada y pagada
- `Cancelled`: Orden cancelada

## 🔄 Flujo de Datos Típico

1. **Cliente HTTP** → Solicitud JSON
2. **Controller** → Validación básica + Deserialización
3. **Service** → Validaciones de negocio + Orquestación
4. **Repository** → Consultas JPA/JPQL + Transacciones
5. **Base de Datos** → Persistencia
6. **Repository** → Retorno de entidad
7. **Service** → Transformación a DTO
8. **Controller** → Serialización + Respuesta HTTP

## 📊 Modelo de Datos

```
Customer (1) ----< (N) Order (1) ----< (N) OrderItem >---- (1) Product
                         |
                         |
                        (1)
                         |
                        \|/
                       (N) Payment
```

### Relaciones:
- Un **Cliente** puede tener muchas **Órdenes**
- Una **Orden** pertenece a un **Cliente**
- Una **Orden** tiene muchos **OrderItems**
- Un **OrderItem** referencia un **Producto**
- Una **Orden** puede tener múltiples **Pagos**

## 🛠️ Tecnologías Utilizadas

- **Jakarta EE 10**: Plataforma empresarial
- **JAX-RS**: API REST
- **JPA/Hibernate**: ORM
- **CDI**: Inyección de dependencias
- **PostgreSQL**: Base de datos relacional
- **WildFly**: Servidor de aplicaciones
- **Maven**: Gestión de dependencias
- **Docker**: Contenedorización de base de datos

## 🐛 Solución de Problemas Comunes

### Error 405 Method Not Allowed
- Verificar que los filtros CORS estén configurados
- Asegurar que `CorsFilter` y `OptionsRequestFilter` estén en el classpath

### Error de Transacción
- Verificar que la conexión a PostgreSQL esté activa
- Revisar logs de WildFly para detalles

### EntityManager NULL
- Verificar que `beans.xml` tenga `bean-discovery-mode="all"`
- Confirmar que JpaProducer esté siendo escaneado

## 📄 Licencia

Este proyecto es parte de un ejercicio académico para LogiTrack Distribution.

## 👥 Autor

Desarrollado como proyecto empresarial de distribución de productos.

---

**Versión**: 1.0.0  
**Última Actualización**: Noviembre 2025