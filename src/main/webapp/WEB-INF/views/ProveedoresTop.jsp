<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Proveedores con más de 2 productos"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Proveedores con más de 2 productos</h1>
<p class="bajada">
    Los proveedores de los que más depende el depósito: si uno falla, hay varios productos
    sin reposición.
</p>

<div class="bloque">
    <c:choose>
        <c:when test="${empty proveedores}">
            <p class="vacio">Ningún proveedor tiene más de dos productos asignados.</p>
        </c:when>
        <c:otherwise>
            <table>
                <caption>
                    Consulta JPQL: SELECT p FROM Proveedor p WHERE SIZE(p.productoProveedorCollection) &gt; 2 ORDER BY p.nombre
                </caption>
                <thead>
                    <tr>
                        <th scope="col">Proveedor</th>
                        <th scope="col">Teléfono</th>
                        <th scope="col">Email</th>
                        <th scope="col">Productos que provee</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="v" items="${proveedores}">
                        <tr>
                            <td>${v.nombre}</td>
                            <td>${v.telefono}</td>
                            <td class="chico">${v.email}</td>
                            <td class="chico">
                                <c:forEach var="pp" items="${v.productoProveedorCollection}" varStatus="s">
                                    ${pp.producto.nombre}<c:if test="${not s.last}">, </c:if>
                                </c:forEach>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%@include file="partes/pie.jsp"%>
