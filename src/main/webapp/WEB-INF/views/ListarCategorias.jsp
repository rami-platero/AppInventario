<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Categorías"/>
<%@include file="partes/cabecera.jsp"%>

<div class="encabezado-bloque">
    <div>
        <h1>Categorías</h1>
        <p class="bajada">Agrupan los productos del catálogo.</p>
    </div>
    <c:if test="${rolActivo eq 'PROPIETARIO'}">
        <a class="btn btn-primario" href="${ctx}/AgregarCategoria">Agregar categoría</a>
    </c:if>
</div>

<div class="bloque">
    <c:choose>
        <c:when test="${empty categorias}">
            <p class="vacio">Todavía no hay categorías cargadas.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th scope="col">Nombre</th>
                        <th scope="col" class="num">Productos</th>
                        <th scope="col">Alertas de stock</th>
                        <th scope="col">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="fila" items="${categorias}">
                        <c:set var="cat" value="${fila[0]}"/>
                        <tr>
                            <td>${cat.nombre}</td>
                            <td class="num">${fila[1]}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty alertas[cat.idcategoria]}">
                                        <span class="badge badge-ok">sin alertas</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-bajo">${alertas[cat.idcategoria]} para reponer</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="acciones">
                                    <a class="btn btn-chico" href="${ctx}/ListarProductos?categoria=${cat.idcategoria}">Ver productos</a>
                                    <c:if test="${rolActivo eq 'PROPIETARIO'}">
                                        <a class="btn btn-chico" href="${ctx}/EditarCategoria?id=${cat.idcategoria}">Editar</a>
                                        <form action="${ctx}/EliminarCategoria" method="post"
                                              onsubmit="return confirm('¿Eliminar la categoría ${cat.nombre}?');">
                                            <input type="hidden" name="id" value="${cat.idcategoria}">
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
