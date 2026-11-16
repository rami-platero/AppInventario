<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Iniciar sesión &mdash; Depósito</title>
        <link rel="stylesheet" href="${ctx}/css/estilos.css">
    </head>
    <body>
        <div class="auth-contenedor">
            <div class="auth-tarjeta">
                <div class="auth-marca">Depósito</div>
                <h1>Iniciar sesión</h1>
                <p class="bajada">Entrá con tu email y tu contraseña.</p>

                <c:if test="${not empty errores}">
                    <div class="aviso">
                        <ul>
                            <c:forEach var="e" items="${errores}">
                                <li><c:out value="${e}"/></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>

                <form class="campos" method="post" action="${ctx}/IniciarSesion">
                    <div class="campo-ancho">
                        <label for="email">Email</label>
                        <input type="text" id="email" name="email" maxlength="80" required autofocus>
                    </div>
                    <div class="campo-ancho">
                        <label for="password">Contraseña</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    <div class="pie-form">
                        <button class="btn btn-primario" type="submit">Entrar</button>
                    </div>
                </form>

                <p class="auth-pie">
                    ¿Todavía no tenés cuenta?
                    <a href="${ctx}/Registrarse">Registrate</a>
                </p>
            </div>
        </div>
    </body>
</html>
