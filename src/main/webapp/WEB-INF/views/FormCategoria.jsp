<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="${empty categoria ? 'Agregar categoría' : 'Editar categoría'}"/>
<%@include file="partes/cabecera.jsp"%>

<h1>${titulo}</h1>
<p class="bajada">El nombre no se puede repetir.</p>

<p class="leyenda">Los campos con <span class="req">*</span> son obligatorios.</p>
<div class="bloque">
    <form class="campos" method="post"
          action="${ctx}/${empty categoria ? 'AgregarCategoria' : 'EditarCategoria'}">
        <c:if test="${not empty categoria}">
            <input type="hidden" name="id" value="${categoria.idcategoria}">
        </c:if>

        <div>
            <label for="nombre">Nombre <span class="req" title="Campo obligatorio">*</span></label>
            <input type="text" id="nombre" name="nombre" value="${categoria.nombre}"
                   maxlength="45" required>
        </div>

        <div class="pie-form">
            <button class="btn btn-primario" type="submit">Guardar</button>
            <a class="btn" href="${ctx}/ListarCategorias">Cancelar</a>
        </div>
    </form>
</div>

<%@include file="partes/pie.jsp"%>
