<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Registrarse &mdash; Depósito</title>
        <link rel="stylesheet" href="${ctx}/css/estilos.css">
    </head>
    <body>
        <div class="auth-contenedor">
            <div class="auth-tarjeta ancha">
                <div class="auth-marca">Depósito</div>
                <h1>Crear una cuenta</h1>
                <p class="bajada">
                    Te registrás y de una vez creás tu negocio. Después podés agregar empleados o
                    sumarte a otros negocios.
                </p>

                <c:if test="${not empty errores}">
                    <div class="aviso">
                        <ul>
                            <c:forEach var="e" items="${errores}">
                                <li><c:out value="${e}"/></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>

                <form class="campos" method="post" action="${ctx}/Registrarse">
                    <div class="campo-ancho">
                        <label for="nombre">Tu nombre <span class="req">*</span></label>
                        <input type="text" id="nombre" name="nombre" maxlength="60" required autofocus>
                    </div>
                    <div class="campo-ancho">
                        <label for="email">Email <span class="req">*</span></label>
                        <input type="text" id="email" name="email" maxlength="80" required>
                    </div>
                    <div class="campo-ancho">
                        <label for="password">Contraseña <span class="req">*</span></label>
                        <input type="password" id="password" name="password" minlength="6" required>
                        <div class="ayuda">Al menos 6 caracteres.</div>
                    </div>
                    <div class="campo-ancho">
                        <label for="nombreNegocio">Nombre de tu negocio <span class="req">*</span></label>
                        <input type="text" id="nombreNegocio" name="nombreNegocio" maxlength="60" required>
                        <div class="ayuda">Vas a ser el propietario de este negocio.</div>
                    </div>
                    <div class="pie-form">
                        <button class="btn btn-primario" type="submit">Crear cuenta</button>
                    </div>
                </form>

                <p class="auth-pie">
                    ¿Ya tenés cuenta?
                    <a href="${ctx}/IniciarSesion">Iniciá sesión</a>
                </p>
            </div>
        </div>
    </body>
</html>
