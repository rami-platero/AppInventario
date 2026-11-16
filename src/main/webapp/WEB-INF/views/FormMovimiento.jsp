<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="titulo" value="Registrar movimiento"/>
<%@include file="partes/cabecera.jsp"%>

<h1>Registrar movimiento</h1>
<p class="bajada">
    Es la única forma de cambiar el stock. Cada movimiento queda guardado con el stock que dejó,
    y todo ocurre en una sola transacción: si algo falla, no se guarda nada.
</p>

<p class="leyenda">Los campos con <span class="req">*</span> son obligatorios.</p>
<div class="bloque">
    <form class="campos" method="post" action="${ctx}/RegistrarMovimiento">
        <div class="campo-ancho">
            <label for="producto">Producto <span class="req" title="Campo obligatorio">*</span></label>
            <select id="producto" name="producto" required>
                <c:forEach var="p" items="${productos}">
                    <option value="${p.idproducto}" data-stock="${p.stock}" data-unidad="${p.unidad}"
                            data-perecedero="${p.perecedero}"
                            ${param.producto eq p.idproducto ? 'selected' : ''}>
                        ${p.nombre} &mdash; ${p.stock} ${p.unidad} en stock<c:if test="${p.perecedero}"> (perecedero)</c:if>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div>
            <label for="tipo">Tipo <span class="req" title="Campo obligatorio">*</span></label>
            <select id="tipo" name="tipo" required>
                <option value="ENTRADA" ${param.tipo eq 'ENTRADA' ? 'selected' : ''}>Entrada &mdash; suma al stock</option>
                <option value="SALIDA" ${empty param.tipo or param.tipo eq 'SALIDA' ? 'selected' : ''}>Salida &mdash; resta del stock</option>
                <option value="AJUSTE" ${param.tipo eq 'AJUSTE' ? 'selected' : ''}>Ajuste &mdash; fija la cantidad exacta</option>
            </select>
        </div>

        <div>
            <label for="cantidad">Cantidad <span class="req" title="Campo obligatorio">*</span></label>
            <input type="number" id="cantidad" name="cantidad"
                   value="${empty param.cantidad ? 1 : param.cantidad}" min="0" step="1" required>
            <p id="vistaPrevia" class="ayuda"></p>
        </div>

        <div id="grupoProveedor">
            <label for="proveedor">Proveedor</label>
            <select id="proveedor" name="proveedor">
                <option value="">Sin especificar</option>
                <c:forEach var="v" items="${proveedores}">
                    <option value="${v.idproveedor}"
                            ${param.proveedor eq v.idproveedor ? 'selected' : ''}>${v.nombre}</option>
                </c:forEach>
            </select>
            <div class="ayuda">Sólo se guarda en las entradas: es a quién se le compró.</div>
        </div>

        <div id="grupoVencimiento" class="oculto">
            <label for="vencimiento">Vencimiento del lote</label>
            <input type="date" id="vencimiento" name="vencimiento">
            <div class="ayuda">Obligatorio en las entradas de productos perecederos.</div>
        </div>

        <div id="grupoCodigoLote" class="oculto">
            <label for="codigoLote">Código del lote</label>
            <input type="text" id="codigoLote" name="codigoLote" maxlength="30">
            <div class="ayuda">Opcional. Si coincide con un lote existente del mismo vencimiento, se suma a ese.</div>
        </div>

        <div id="grupoLote" class="oculto">
            <label for="lote">Lote</label>
            <select id="lote" name="lote">
                <option value="" data-producto="">El que vence primero (FEFO)</option>
                <c:forEach var="l" items="${lotes}">
                    <option value="${l.idlote}" data-producto="${l.productoIdproducto.idproducto}"
                            ${param.lote eq l.idlote ? 'selected' : ''}>
                        vence <fmt:formatDate value="${l.vencimiento}" pattern="dd/MM/yyyy"/>
                        &mdash; ${l.cantidad} ${l.productoIdproducto.unidad}<c:if test="${not empty l.codigo}"> &middot; ${l.codigo}</c:if>
                    </option>
                </c:forEach>
            </select>
            <div class="ayuda" id="ayudaLote"></div>
        </div>

        <div class="campo-ancho">
            <label for="motivo">Motivo</label>
            <input type="text" id="motivo" name="motivo" maxlength="200"
                   placeholder="Venta en mostrador, rotura, recuento de inventario...">
        </div>

        <div class="pie-form">
            <button class="btn btn-primario" type="submit">Registrar</button>
            <a class="btn" href="${ctx}/ListarMovimientos">Cancelar</a>
        </div>
    </form>
</div>

<script>
    (function () {
        var producto = document.getElementById('producto');
        var tipo = document.getElementById('tipo');
        var cantidad = document.getElementById('cantidad');
        var salida = document.getElementById('vistaPrevia');
        var grupoProveedor = document.getElementById('grupoProveedor');
        var grupoVencimiento = document.getElementById('grupoVencimiento');
        var grupoCodigoLote = document.getElementById('grupoCodigoLote');
        var grupoLote = document.getElementById('grupoLote');
        var vencimiento = document.getElementById('vencimiento');
        var lote = document.getElementById('lote');
        var ayudaLote = document.getElementById('ayudaLote');

        function mostrar(elemento, visible) {
            elemento.className = visible ? '' : 'oculto';
        }

        function filtrarLotes(idProducto) {
            var disponibles = 0;
            for (var i = 0; i < lote.options.length; i++) {
                var opcion = lote.options[i];
                var suyo = opcion.getAttribute('data-producto');
                if (suyo === '') {
                    continue;
                }
                var propio = suyo === idProducto;
                opcion.hidden = !propio;
                opcion.disabled = !propio;
                if (propio) {
                    disponibles++;
                }
            }
            return disponibles;
        }

        function actualizar() {
            var opcion = producto.options[producto.selectedIndex];
            if (!opcion) {
                return;
            }
            var stock = parseInt(opcion.getAttribute('data-stock'), 10);
            var unidad = opcion.getAttribute('data-unidad');
            var perecedero = opcion.getAttribute('data-perecedero') === 'true';
            var idProducto = opcion.value;
            var esEntrada = tipo.value === 'ENTRADA';
            var esAjuste = tipo.value === 'AJUSTE';

            mostrar(grupoProveedor, esEntrada);
            mostrar(grupoVencimiento, perecedero && esEntrada);
            mostrar(grupoCodigoLote, perecedero && esEntrada);
            mostrar(grupoLote, perecedero && !esEntrada);
            vencimiento.required = perecedero && esEntrada;

            var disponibles = 0;
            if (perecedero && !esEntrada) {
                disponibles = filtrarLotes(idProducto);
                var elegido = lote.options[lote.selectedIndex];
                if (!elegido || elegido.disabled) {
                    lote.value = '';
                }
                lote.required = esAjuste;
                if (esAjuste) {
                    ayudaLote.textContent = 'El ajuste fija la cantidad de un lote puntual, así que hay que elegir cuál.';
                } else {
                    ayudaLote.textContent = 'Si no elegís ninguno, se descuenta del lote que vence primero.';
                }
                if (disponibles === 0) {
                    ayudaLote.textContent = 'Este producto no tiene lotes con stock: primero registrá una entrada.';
                }
            } else {
                lote.required = false;
            }

            var n = parseInt(cantidad.value, 10);
            if (isNaN(n)) {
                n = 0;
            }

            if (perecedero && esAjuste) {
                salida.className = 'ayuda';
                salida.textContent = 'El stock del producto pasa a ser la suma de sus lotes.';
                return;
            }

            var resultado = esEntrada ? stock + n : (esAjuste ? n : stock - n);
            if (resultado < 0) {
                salida.className = 'ayuda aviso';
                salida.textContent = 'No alcanza: hay ' + stock + ' ' + unidad + ' y se quieren retirar ' + n + '.';
            } else {
                salida.className = 'ayuda';
                salida.textContent = stock + ' ' + unidad + ' → quedan ' + resultado + ' ' + unidad + '.';
            }
        }

        producto.addEventListener('change', actualizar);
        tipo.addEventListener('change', actualizar);
        cantidad.addEventListener('input', actualizar);
        actualizar();
    })();
</script>

<%@include file="partes/pie.jsp"%>
