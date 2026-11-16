<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Tus negocios &mdash; Depósito</title>
        <link rel="stylesheet" href="${ctx}/css/estilos.css">
    </head>
    <body>
        <div class="auth-contenedor">
            <div class="auth-tarjeta ancha">
                <div class="auth-marca">Depósito</div>
                <h1>Tus negocios</h1>
                <p class="bajada">Elegí con cuál querés trabajar.</p>

                <c:if test="${not empty param.error}">
                    <p class="aviso"><c:out value="${param.error}"/></p>
                </c:if>

                <c:choose>
                    <c:when test="${empty membresias}">
                        <p class="vacio">Todavía no pertenecés a ningún negocio.</p>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${ctx}/Negocios">
                            <c:forEach var="m" items="${membresias}">
                                <div class="negocio-fila">
                                    <div>
                                        <div class="nombre">${m.negocio.nombre}</div>
                                        <div class="rol">${m.rol eq 'PROPIETARIO' ? 'Propietario' : 'Empleado'}</div>
                                    </div>
                                    <button class="btn btn-chico" type="submit"
                                            name="idNegocio" value="${m.negocio.idnegocio}">Entrar</button>
                                </div>
                            </c:forEach>
                        </form>
                    </c:otherwise>
                </c:choose>

                <h2 style="font-size:16px;margin:26px 0 12px;">Crear un negocio nuevo</h2>
                <form class="campos" method="post" action="${ctx}/CrearNegocio">
                    <div class="campo-ancho">
                        <label for="nombre">Nombre del negocio</label>
                        <input type="text" id="nombre" name="nombre" maxlength="60" required>
                    </div>
                    <div class="pie-form">
                        <button class="btn btn-primario" type="submit">Crear y entrar</button>
                    </div>
                </form>

                <p class="auth-pie">
                    <a href="${ctx}/CerrarSesion">Cerrar sesión</a>
                </p>
            </div>
        </div>
    </body>
</html>
