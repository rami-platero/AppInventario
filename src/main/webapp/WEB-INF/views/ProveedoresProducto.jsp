<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Proveedores del producto"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Proveedores de ${producto.nombre}</h1>
<p class="bajada">
    Un producto se puede comprar a varios proveedores, cada uno con su precio y su demora.
    El más barato es el que se sugiere al reponer.
</p>

<div class="dos-columnas">
    <div class="bloque">
        <h2>Proveedores asignados</h2>
        <c:choose>
            <c:when test="${empty asignados}">
                <p class="vacio">Todavía no hay proveedores asignados a este producto.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <caption>Ordenados del más barato al más caro.</caption>
                    <thead>
                        <tr>
                            <th scope="col">Proveedor</th>
                            <th scope="col" class="num">Precio compra</th>
                            <th scope="col" class="num">Demora</th>
                            <th scope="col">Código</th>
                            <th scope="col"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="pp" items="${asignados}" varStatus="s">
                            <tr>
                                <td>
                                    ${pp.proveedor.nombre}
                                    <c:if test="${s.first}">
                                        <span class="badge badge-ok">más barato</span>
                                    </c:if>
                                    <div class="chico">${pp.proveedor.telefono}</div>
                                </td>
                                <td class="num"><fmt:formatNumber value="${pp.precioCompra}" type="currency" currencySymbol="$"/></td>
                                <td class="num">
                                    <c:choose>
                                        <c:when test="${empty pp.diasEntrega}">&mdash;</c:when>
                                        <c:otherwise>${pp.diasEntrega} días</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="chico">${pp.codigoProveedor}</td>
                                <td>
                                    <form action="${ctx}/EliminarProveedorProducto" method="post"
                                          onsubmit="return confirm('¿Quitar a ${pp.proveedor.nombre} de este producto?');">
                                        <input type="hidden" name="idProducto" value="${producto.idproducto}">
                                        <input type="hidden" name="idProveedor" value="${pp.proveedor.idproveedor}">
                                        <button class="btn btn-chico btn-peligro" type="submit">Quitar</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="bloque">
        <h2>Asignar un proveedor</h2>
        <form class="campos" method="post" action="${ctx}/AgregarProveedorProducto">
            <input type="hidden" name="idProducto" value="${producto.idproducto}">

            <div class="campo-ancho">
                <label for="idProveedor">Proveedor <span class="req" title="Campo obligatorio">*</span></label>
                <select id="idProveedor" name="idProveedor" required>
                    <c:forEach var="v" items="${proveedores}">
                        <option value="${v.idproveedor}">${v.nombre}</option>
                    </c:forEach>
                </select>
                <div class="ayuda">Si el proveedor ya está asignado, quitalo primero para cambiarle el precio.</div>
            </div>

            <div>
                <label for="precioCompra">Precio de compra <span class="req" title="Campo obligatorio">*</span></label>
                <input type="number" id="precioCompra" name="precioCompra" value="0.00"
                       min="0" step="0.01" required>
            </div>

            <div>
                <label for="diasEntrega">Días de entrega</label>
                <input type="number" id="diasEntrega" name="diasEntrega" min="0" step="1">
            </div>

            <div class="campo-ancho">
                <label for="codigoProveedor">Código del proveedor</label>
                <input type="text" id="codigoProveedor" name="codigoProveedor" maxlength="30">
                <div class="ayuda">Cómo identifica el proveedor a este producto en su catálogo.</div>
            </div>

            <div class="pie-form">
                <button class="btn btn-primario" type="submit">Asignar</button>
                <a class="btn" href="${ctx}/ListarProductos">Volver</a>
            </div>
        </form>
    </div>
</div>

<%@include file="partes/pie.jsp"%>
