# DefaultApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**asignarPrioridad**](DefaultApi.md#asignarPrioridad) | **PUT** /api/solicitudes/{id}/prioridad | Asignar prioridad a una solicitud |
| [**asignarResponsable**](DefaultApi.md#asignarResponsable) | **PATCH** /api/solicitudes/{id}/asignar | Asignar responsable a una solicitud |
| [**cambiarEstado**](DefaultApi.md#cambiarEstado) | **PATCH** /api/solicitudes/{id}/estado | Cambiar el estado de una solicitud |
| [**cerrarSolicitud**](DefaultApi.md#cerrarSolicitud) | **PUT** /api/solicitudes/{id}/cerrar | Cerrar una solicitud |
| [**clasificarSolicitud**](DefaultApi.md#clasificarSolicitud) | **PATCH** /api/solicitudes/{id}/clasificar | Clasificar una solicitud |
| [**generarResumen**](DefaultApi.md#generarResumen) | **GET** /api/solicitudes/{id}/resumen | Generar resumen de una solicitud |
| [**listarSolicitudes**](DefaultApi.md#listarSolicitudes) | **GET** /api/solicitudes | Listar solicitudes con filtros |
| [**obtenerHistorial**](DefaultApi.md#obtenerHistorial) | **GET** /api/solicitudes/{id}/historial | Obtener historial de una solicitud |
| [**obtenerSolicitudPorId**](DefaultApi.md#obtenerSolicitudPorId) | **GET** /api/solicitudes/{id} | Obtener solicitud por id |
| [**registrarSolicitud**](DefaultApi.md#registrarSolicitud) | **POST** /api/solicitudes | Registrar una nueva solicitud |
| [**sugerirClasificacion**](DefaultApi.md#sugerirClasificacion) | **POST** /api/solicitudes/sugerir-clasificacion | Generar sugerencia automática de clasificación |


<a id="asignarPrioridad"></a>
# **asignarPrioridad**
> SolicitudResponse asignarPrioridad(id, usuarioId, asignarPrioridadRequest)

Asignar prioridad a una solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 56L; // Long | 
    Long usuarioId = 56L; // Long | 
    AsignarPrioridadRequest asignarPrioridadRequest = new AsignarPrioridadRequest(); // AsignarPrioridadRequest | 
    try {
      SolicitudResponse result = apiInstance.asignarPrioridad(id, usuarioId, asignarPrioridadRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#asignarPrioridad");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**|  | |
| **usuarioId** | **Long**|  | |
| **asignarPrioridadRequest** | [**AsignarPrioridadRequest**](AsignarPrioridadRequest.md)|  | |

### Return type

[**SolicitudResponse**](SolicitudResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Prioridad asignada correctamente |  -  |
| **403** | Usuario no autorizado |  -  |

<a id="asignarResponsable"></a>
# **asignarResponsable**
> SolicitudResponse asignarResponsable(id, usuarioId, asignarResponsableRequest)

Asignar responsable a una solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 56L; // Long | 
    Long usuarioId = 56L; // Long | 
    AsignarResponsableRequest asignarResponsableRequest = new AsignarResponsableRequest(); // AsignarResponsableRequest | 
    try {
      SolicitudResponse result = apiInstance.asignarResponsable(id, usuarioId, asignarResponsableRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#asignarResponsable");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**|  | |
| **usuarioId** | **Long**|  | |
| **asignarResponsableRequest** | [**AsignarResponsableRequest**](AsignarResponsableRequest.md)|  | |

### Return type

[**SolicitudResponse**](SolicitudResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Responsable asignado correctamente |  -  |
| **403** | Usuario no autorizado |  -  |

<a id="cambiarEstado"></a>
# **cambiarEstado**
> SolicitudResponse cambiarEstado(id, cambiarEstadoRequest)

Cambiar el estado de una solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 56L; // Long | 
    CambiarEstadoRequest cambiarEstadoRequest = new CambiarEstadoRequest(); // CambiarEstadoRequest | 
    try {
      SolicitudResponse result = apiInstance.cambiarEstado(id, cambiarEstadoRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#cambiarEstado");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**|  | |
| **cambiarEstadoRequest** | [**CambiarEstadoRequest**](CambiarEstadoRequest.md)|  | |

### Return type

[**SolicitudResponse**](SolicitudResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Estado cambiado correctamente |  -  |
| **409** | Transición inválida |  -  |

<a id="cerrarSolicitud"></a>
# **cerrarSolicitud**
> SolicitudResponse cerrarSolicitud(id, usuarioId, cerrarSolicitudRequest)

Cerrar una solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 56L; // Long | 
    Long usuarioId = 56L; // Long | 
    CerrarSolicitudRequest cerrarSolicitudRequest = new CerrarSolicitudRequest(); // CerrarSolicitudRequest | 
    try {
      SolicitudResponse result = apiInstance.cerrarSolicitud(id, usuarioId, cerrarSolicitudRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#cerrarSolicitud");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**|  | |
| **usuarioId** | **Long**|  | |
| **cerrarSolicitudRequest** | [**CerrarSolicitudRequest**](CerrarSolicitudRequest.md)|  | |

### Return type

[**SolicitudResponse**](SolicitudResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Solicitud cerrada correctamente |  -  |
| **403** | Usuario no autorizado |  -  |

<a id="clasificarSolicitud"></a>
# **clasificarSolicitud**
> SolicitudResponse clasificarSolicitud(id, usuarioId, clasificarSolicitudRequest)

Clasificar una solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 56L; // Long | 
    Long usuarioId = 56L; // Long | 
    ClasificarSolicitudRequest clasificarSolicitudRequest = new ClasificarSolicitudRequest(); // ClasificarSolicitudRequest | 
    try {
      SolicitudResponse result = apiInstance.clasificarSolicitud(id, usuarioId, clasificarSolicitudRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#clasificarSolicitud");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**|  | |
| **usuarioId** | **Long**|  | |
| **clasificarSolicitudRequest** | [**ClasificarSolicitudRequest**](ClasificarSolicitudRequest.md)|  | |

### Return type

[**SolicitudResponse**](SolicitudResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Solicitud clasificada correctamente |  -  |
| **409** | Transición inválida |  -  |
| **403** | Usuario no autorizado |  -  |

<a id="generarResumen"></a>
# **generarResumen**
> String generarResumen(id)

Generar resumen de una solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 56L; // Long | 
    try {
      String result = apiInstance.generarResumen(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#generarResumen");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**|  | |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Resumen generado correctamente |  -  |

<a id="listarSolicitudes"></a>
# **listarSolicitudes**
> List&lt;SolicitudResponse&gt; listarSolicitudes(estado, tipo, prioridad, responsableId)

Listar solicitudes con filtros

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    EstadoSolicitud estado = EstadoSolicitud.fromValue("REGISTRADA"); // EstadoSolicitud | 
    TipoSolicitud tipo = TipoSolicitud.fromValue("REGISTRO_ASIGNATURAS"); // TipoSolicitud | 
    Prioridad prioridad = Prioridad.fromValue("BAJA"); // Prioridad | 
    Long responsableId = 56L; // Long | 
    try {
      List<SolicitudResponse> result = apiInstance.listarSolicitudes(estado, tipo, prioridad, responsableId);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#listarSolicitudes");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **estado** | [**EstadoSolicitud**](.md)|  | [optional] [enum: REGISTRADA, CLASIFICADA, EN_ATENCION, ATENDIDA, CERRADA] |
| **tipo** | [**TipoSolicitud**](.md)|  | [optional] [enum: REGISTRO_ASIGNATURAS, HOMOLOGACION, CANCELACION_ASIGNATURAS, SOLICITUD_CUPOS, CONSULTA_ACADEMICA] |
| **prioridad** | [**Prioridad**](.md)|  | [optional] [enum: BAJA, MEDIA, ALTA, CRITICA] |
| **responsableId** | **Long**|  | [optional] |

### Return type

[**List&lt;SolicitudResponse&gt;**](SolicitudResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Lista de solicitudes |  -  |

<a id="obtenerHistorial"></a>
# **obtenerHistorial**
> List&lt;HistorialSolicitudResponse&gt; obtenerHistorial(id)

Obtener historial de una solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 56L; // Long | 
    try {
      List<HistorialSolicitudResponse> result = apiInstance.obtenerHistorial(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#obtenerHistorial");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**|  | |

### Return type

[**List&lt;HistorialSolicitudResponse&gt;**](HistorialSolicitudResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Historial encontrado |  -  |

<a id="obtenerSolicitudPorId"></a>
# **obtenerSolicitudPorId**
> SolicitudResponse obtenerSolicitudPorId(id)

Obtener solicitud por id

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 56L; // Long | 
    try {
      SolicitudResponse result = apiInstance.obtenerSolicitudPorId(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#obtenerSolicitudPorId");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**|  | |

### Return type

[**SolicitudResponse**](SolicitudResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Solicitud encontrada |  -  |
| **404** | Solicitud no encontrada |  -  |

<a id="registrarSolicitud"></a>
# **registrarSolicitud**
> SolicitudResponse registrarSolicitud(solicitudCreateRequest)

Registrar una nueva solicitud

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    SolicitudCreateRequest solicitudCreateRequest = new SolicitudCreateRequest(); // SolicitudCreateRequest | 
    try {
      SolicitudResponse result = apiInstance.registrarSolicitud(solicitudCreateRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#registrarSolicitud");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **solicitudCreateRequest** | [**SolicitudCreateRequest**](SolicitudCreateRequest.md)|  | |

### Return type

[**SolicitudResponse**](SolicitudResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Solicitud creada correctamente |  -  |

<a id="sugerirClasificacion"></a>
# **sugerirClasificacion**
> SugerenciaClasificacionResponse sugerirClasificacion(sugerirClasificacionRequest)

Generar sugerencia automática de clasificación

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    SugerirClasificacionRequest sugerirClasificacionRequest = new SugerirClasificacionRequest(); // SugerirClasificacionRequest | 
    try {
      SugerenciaClasificacionResponse result = apiInstance.sugerirClasificacion(sugerirClasificacionRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#sugerirClasificacion");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **sugerirClasificacionRequest** | [**SugerirClasificacionRequest**](SugerirClasificacionRequest.md)|  | |

### Return type

[**SugerenciaClasificacionResponse**](SugerenciaClasificacionResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Sugerencia generada correctamente |  -  |

