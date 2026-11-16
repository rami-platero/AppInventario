<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="${empty proveedor ? 'Agregar proveedor' : 'Editar proveedor'}"/>
<%@include file="partes/cabecera.jsp"%>

<h1>${titulo}</h1>
<p class="bajada">Los productos que le comprás se asignan desde cada producto.</p>

<p class="leyenda">Los campos con <span class="req">*</span> son obligatorios.</p>
<div class="bloque">
    <form class="campos" method="post"
          action="${ctx}/${empty proveedor ? 'AgregarProveedor' : 'EditarProveedor'}">
        <c:if test="${not empty proveedor}">
            <input type="hidden" name="id" value="${proveedor.idproveedor}">
        </c:if>

        <div>
            <label for="nombre">Nombre <span class="req" title="Campo obligatorio">*</span></label>
            <input type="text" id="nombre" name="nombre" value="${proveedor.nombre}"
                   maxlength="45" required>
        </div>

        <div>
            <label for="telefono">Teléfono</label>
            <input type="text" id="telefono" name="telefono" value="${proveedor.telefono}"
                   maxlength="20">
        </div>

        <div>
            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="${proveedor.email}"
                   maxlength="60">
        </div>

        <div class="pie-form">
            <button class="btn btn-primario" type="submit">Guardar</button>
            <a class="btn" href="${ctx}/ListarProveedores">Cancelar</a>
        </div>
    </form>
</div>

<%@include file="partes/pie.jsp"%>
