<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="titulo" value="Empleados"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Empleados</h1>
<p class="bajada">
    Quién puede entrar a este negocio y qué puede hacer. El propietario administra todo;
    el empleado registra movimientos y consulta, pero no gestiona el catálogo ni a la gente.
</p>

<div class="dos-columnas">
    <div class="bloque">
        <h2>Miembros de este negocio</h2>
        <c:choose>
            <c:when test="${empty membresias}">
                <p class="vacio">No hay nadie cargado todavía.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th scope="col">Nombre</th>
                            <th scope="col">Email</th>
                            <th scope="col">Rol</th>
                            <th scope="col"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="m" items="${membresias}">
                            <tr>
                                <td>
                                    ${m.usuario.nombre}
                                    <c:if test="${m.usuario.idusuario eq usuarioActual.idusuario}">
                                        <span class="badge badge-neutro">vos</span>
                                    </c:if>
                                </td>
                                <td class="chico">${m.usuario.email}</td>
                                <td>
                                    <form action="${ctx}/CambiarRolEmpleado" method="post" style="display:inline">
                                        <input type="hidden" name="idUsuario" value="${m.usuario.idusuario}">
                                        <select name="rol" onchange="this.form.submit()">
                                            <option value="PROPIETARIO" ${m.rol eq 'PROPIETARIO' ? 'selected' : ''}>Propietario</option>
                                            <option value="EMPLEADO" ${m.rol eq 'EMPLEADO' ? 'selected' : ''}>Empleado</option>
                                        </select>
                                    </form>
                                </td>
                                <td>
                                    <form action="${ctx}/EliminarEmpleado" method="post"
                                          onsubmit="return confirm('¿Quitar a ${m.usuario.nombre} de este negocio?');">
                                        <input type="hidden" name="idUsuario" value="${m.usuario.idusuario}">
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
        <h2>Agregar a alguien</h2>
        <p class="leyenda">La persona tiene que haberse registrado antes con ese email.</p>
        <form class="campos" method="post" action="${ctx}/AgregarEmpleado">
            <div class="campo-ancho">
                <label for="email">Email <span class="req">*</span></label>
                <input type="text" id="email" name="email" maxlength="80" required>
            </div>
            <div>
                <label for="rol">Rol <span class="req">*</span></label>
                <select id="rol" name="rol" required>
                    <option value="EMPLEADO">Empleado</option>
                    <option value="PROPIETARIO">Propietario</option>
                </select>
            </div>
            <div class="pie-form">
                <button class="btn btn-primario" type="submit">Agregar</button>
            </div>
        </form>
    </div>
</div>

<%@include file="partes/pie.jsp"%>
