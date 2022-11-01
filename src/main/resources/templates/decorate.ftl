<#function yyyyMMdd d>
  <#if d?trim?length != 8>
    <#return d>
  </#if>
  <#return d[0..3]+"/"+d[4..5]+"/"+d[6..7]>
</#function>

<#function yyMMdd d>
  <#if d?trim?length != 6>
    <#return d>
  </#if>
  <#return d[0..1]+"/"+d[2..3]+"/"+d[4..5]>
</#function>

<#function hhmmss t>
  <#if t?trim?length != 6>
    <#return t>
  </#if>
  <#return t[0..1]+":"+t[2..3]+":"+t[4..5]>
</#function>