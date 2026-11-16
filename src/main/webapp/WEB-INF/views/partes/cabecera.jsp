<fmt:setLocale value="es_AR"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>${titulo} &mdash; Depósito</title>
        <link rel="stylesheet" href="${ctx}/css/estilos.css">
    </head>
    <body>
        <div class="contenedor">
            <nav class="lateral">
                <div class="marca">
                    Depósito
                    <span>Inventario de stock</span>
                </div>
                <div class="menu">
                    <h3>General</h3>
                    <a href="${ctx}/Panel">Panel</a>
                    <a href="${ctx}/Reponer">
                        Hay que reponer
                        <c:if test="${contadorReponer gt 0}">
                            <span class="contador">${contadorReponer}</span>
                        </c:if>
                    </a>

                    <h3>Catálogo</h3>
                    <a href="${ctx}/ListarProductos">Productos</a>
                    <a href="${ctx}/ListarCategorias">Categorías</a>
                    <a href="${ctx}/ListarProveedores">Proveedores</a>

                    <h3>Stock</h3>
                    <a href="${ctx}/ListarMovimientos">Movimientos</a>
                    <a href="${ctx}/RegistrarMovimiento">Registrar movimiento</a>
                    <a href="${ctx}/Vencimientos">
                        Vencimientos
                        <c:if test="${contadorPorVencer gt 0}">
                            <span class="contador">${contadorPorVencer}</span>
                        </c:if>
                    </a>

                    <h3>Consultas</h3>
                    <a href="${ctx}/MasVendidos">Más vendidos</a>
                    <a href="${ctx}/ComprasPorProveedor">Compras por proveedor</a>
                    <a href="${ctx}/ProveedoresTop">Proveedores con más de 2</a>
                    <a href="${ctx}/SinProveedor">Productos sin proveedor</a>
                </div>
            </nav>
            <main class="principal">
                <c:if test="${not empty param.error}">
                    <p class="aviso"><c:out value="${param.error}"/></p>
                </c:if>
                <c:if test="${not empty errores}">
                    <div class="aviso">
                        <strong>No se pudo guardar. Revisá esto:</strong>
                        <ul>
                            <c:forEach var="e" items="${errores}">
                                <li><c:out value="${e}"/></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
