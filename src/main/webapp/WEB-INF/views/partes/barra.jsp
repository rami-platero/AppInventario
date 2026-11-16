<c:set var="objetivo" value="${p.stockMinimo gt 0 ? p.stockMinimo * 2 : 1}"/>
<c:set var="porcentaje" value="${p.stock * 100 / objetivo}"/>
<span class="barra ${p.stock eq 0 ? 'agotado' : (p.stock le p.stockMinimo ? 'bajo' : '')}"
      title="Objetivo de reposición: ${objetivo} ${p.unidad}"><span
        style="width:<fmt:formatNumber value="${porcentaje gt 100 ? 100 : porcentaje}" maxFractionDigits="0"/>%"></span></span>
