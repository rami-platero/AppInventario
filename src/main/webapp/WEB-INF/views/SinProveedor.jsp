<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Productos sin proveedor"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Productos sin proveedor</h1>
<p class="bajada">
    Productos a los que no se les asignó ningún proveedor. Si hay que reponerlos,
    el sistema no puede sugerir a quién comprarles.
</p>

<div class="bloque">
    <c:choose>
        <c:when test="${empty productos}">
            <p class="vacio">Todos los productos tienen al menos un proveedor asignado.</p>
        </c:when>
        <c:otherwise>
            <table>
                <caption>
                    Consulta JPQL: SELECT p FROM Producto p WHERE p.productoProveedorCollection IS EMPTY ORDER BY p.nombre
                </caption>
                <thead>
                    <tr>
                        <th scope="col">Producto</th>
                        <th scope="col">Categoría</th>
                        <th scope="col" class="num">Stock</th>
                        <th scope="col">Estado</th>
                        <th scope="col"></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${productos}">
                        <tr>
                            <td>
                                ${p.nombre}
                                <div class="chico">${p.codigo}</div>
                            </td>
                            <td>${p.categoriaIdcategoria.nombre}</td>
                            <td class="num">${p.stock} / ${p.stockMinimo}</td>
                            <td><%@include file="partes/estado.jsp"%></td>
                            <td>
                                <a class="btn btn-chico btn-primario" href="${ctx}/ProveedoresProducto?id=${p.idproducto}">Asignar proveedor</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%@include file="partes/pie.jsp"%>
