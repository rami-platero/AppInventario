<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Algo salió mal"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Algo salió mal</h1>
<p class="bajada">
    La operación no se pudo completar y no se guardó ningún cambio.
</p>

<div class="bloque">
    <c:choose>
        <c:when test="${pageContext.errorData.statusCode eq 404}">
            <p>La dirección <strong><c:out value="${pageContext.errorData.requestURI}"/></strong>
                no existe en esta aplicación.</p>
        </c:when>
        <c:otherwise>
            <p>Ocurrió un error inesperado procesando
                <strong><c:out value="${pageContext.errorData.requestURI}"/></strong>.
                El detalle quedó registrado en el log del servidor.</p>
        </c:otherwise>
    </c:choose>
    <p class="acciones">
        <a class="btn btn-primario" href="${ctx}/Panel">Volver al panel</a>
        <a class="btn" href="${ctx}/ListarProductos">Ir a productos</a>
    </p>
</div>

<%@include file="partes/pie.jsp"%>
