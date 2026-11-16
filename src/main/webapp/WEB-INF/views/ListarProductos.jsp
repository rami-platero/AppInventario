<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<c:set var="titulo" value="Productos"/>
<%@include file="partes/cabecera.jsp"%>

<div class="encabezado-bloque">
    <div>
        <h1>Productos</h1>
        <p class="bajada">Alta, edición y baja del catálogo.</p>
    </div>
    <a class="btn btn-primario" href="${ctx}/AgregarProducto">Agregar producto</a>
</div>

<form class="filtros" method="get" action="${ctx}/ListarProductos">
    <div>
        <label for="texto">Buscar</label>
        <input type="text" id="texto" name="texto" value="${param.texto}"
               placeholder="Nombre o código" maxlength="60">
    </div>
    <div>
        <label for="categoria">Categoría</label>
        <select id="categoria" name="categoria">
            <option value="">Todas</option>
            <c:forEach var="cat" items="${categorias}">
                <option value="${cat.idcategoria}"
                        ${param.categoria eq cat.idcategoria ? 'selected' : ''}>${cat.nombre}</option>
            </c:forEach>
        </select>
    </div>
    <div>
        <label for="estado">Estado</label>
        <select id="estado" name="estado">
            <option value="" ${empty param.estado ? 'selected' : ''}>Todos</option>
            <option value="reponer" ${param.estado eq 'reponer' ? 'selected' : ''}>Para reponer</option>
            <option value="agotado" ${param.estado eq 'agotado' ? 'selected' : ''}>Agotados</option>
            <option value="normal" ${param.estado eq 'normal' ? 'selected' : ''}>Stock normal</option>
        </select>
    </div>
    <div>
        <label for="orden">Ordenar por</label>
        <select id="orden" name="orden">
            <option value="nombre" ${empty param.orden or param.orden eq 'nombre' ? 'selected' : ''}>Nombre</option>
            <option value="stock" ${param.orden eq 'stock' ? 'selected' : ''}>Stock</option>
            <option value="precio" ${param.orden eq 'precio' ? 'selected' : ''}>Precio</option>
            <option value="categoria" ${param.orden eq 'categoria' ? 'selected' : ''}>Categoría</option>
        </select>
    </div>
    <button class="btn" type="submit">Filtrar</button>
    <a class="btn" href="${ctx}/ListarProductos">Limpiar</a>
</form>

<div class="bloque">
    <c:choose>
        <c:when test="${empty productos}">
            <p class="vacio">No hay productos que coincidan con la búsqueda.</p>
        </c:when>
        <c:otherwise>
            <table>
                <caption>${fn:length(productos)} productos.</caption>
                <thead>
                    <tr>
                        <th scope="col">Producto</th>
                        <th scope="col">Categoría</th>
                        <th scope="col" class="num">Stock</th>
                        <th scope="col">Estado</th>
                        <th scope="col" class="num">Precio</th>
                        <th scope="col">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${productos}">
                        <tr class="${p.stock eq 0 ? 'fila-agotada' : (p.stock le p.stockMinimo ? 'fila-alerta' : '')}">
                            <td>
                                ${p.nombre}
                                <div class="chico">
                                    ${p.codigo} &middot; ${p.unidad}<c:if test="${p.perecedero}"> &middot; por lotes</c:if>
                                </div>
                            </td>
                            <td>${p.categoriaIdcategoria.nombre}</td>
                            <td class="num">
                                ${p.stock} / ${p.stockMinimo}
                                <%@include file="partes/barra.jsp"%>
                            </td>
                            <td><%@include file="partes/estado.jsp"%></td>
                            <td class="num"><fmt:formatNumber value="${p.precio}" type="currency" currencySymbol="$"/></td>
                            <td>
                                <div class="acciones">
                                    <a class="btn btn-chico" href="${ctx}/EditarProducto?id=${p.idproducto}">Editar</a>
                                    <a class="btn btn-chico" href="${ctx}/ProveedoresProducto?id=${p.idproducto}">Proveedores</a>
                                    <a class="btn btn-chico" href="${ctx}/RegistrarMovimiento?producto=${p.idproducto}">Mover</a>
                                    <form action="${ctx}/EliminarProducto" method="post"
                                          onsubmit="return confirm('¿Eliminar ${p.nombre}?');">
                                        <input type="hidden" name="id" value="${p.idproducto}">
                                        <button class="btn btn-chico btn-peligro" type="submit">Eliminar</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%@include file="partes/pie.jsp"%>
