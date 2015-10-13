<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Show Cookie</title>
    <script src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
	<style type="text/css">
		table {margin-top: 30px}
		thead tr {font-size: 22px}
	</style>
  </head>
  <body>
    <table align="center" border="1px" width="888px">
    	<thead>
    		<tr>
    			<th colspan="3">Cookies</th>
    		</tr>
    		<tr>
    			<th width="20%">URL</th>
    			<th width="60%">Cookie</th>
    			<th width="20%">ReceiveTime</th>
    		</tr>
    	</thead>
    	<tbody>
    		<#if cookies?? && cookies?size gt 0>
    		<#list cookies as cookie>
    			<tr>
    				<td style="text-align: left;">${cookie.refererUrl!''}</td>
    				<td style="text-align: left;">${cookie.cookie!''}</td>
    				<td style="text-align: center;">${(cookie.createTime!'')?string('yyyy-MM-dd HH:mm:ss')}</td>
    			</tr>
			</#list>
    		<#else>
    			<tr>
    				<td colspan="3" style="text-align: center; color: red">没有获取到Cookie！</td>
    			</tr>
    		</#if>
    	</tbody>
    </table>
  </body>
</html>
