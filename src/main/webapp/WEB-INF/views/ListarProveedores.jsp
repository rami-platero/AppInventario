<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Proveedores"/>
<%@include file="partes/cabecera.jsp"%>

<div class="encabezado-bloque">
    <div>
        <h1>Proveedores</h1>
        <p class="bajada">A quién se le compra cada producto.</p>
    </div>
    <c:if test="${rolActivo eq 'PROPIETARIO'}">
        <a class="btn btn-primario" href="${ctx}/AgregarProveedor">Agregar proveedor</a>
    </c:if>
</div>

<div class="bloque">
    <c:choose>
        <c:when test="${empty proveedores}">
            <p class="vacio">Todavía no hay proveedores cargados.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th scope="col">Nombre</th>
                        <th scope="col">Teléfono</th>
                        <th scope="col">Email</th>
                        <th scope="col" class="num">Productos</th>
                        <th scope="col">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="fila" items="${proveedores}">
                        <c:set var="v" value="${fila[0]}"/>
                        <tr>
                            <td>${v.nombre}</td>
                            <td>${v.telefono}</td>
                            <td class="chico">${v.email}</td>
                            <td class="num">${fila[1]}</td>
                            <td>
                                <div class="acciones">
                                    <c:if test="${rolActivo eq 'PROPIETARIO'}">
                                        <a class="btn btn-chico" href="${ctx}/EditarProveedor?id=${v.idproveedor}">Editar</a>
                                        <form action="${ctx}/EliminarProveedor" method="post"
                                              onsubmit="return confirm('¿Eliminar al proveedor ${v.nombre}?');">
                                            <input type="hidden" name="id" value="${v.idproveedor}">
                                            <button class="btn btn-chico btn-peligro" type="submit">Eliminar</button>
                                        </form>
                                    </c:if>
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
