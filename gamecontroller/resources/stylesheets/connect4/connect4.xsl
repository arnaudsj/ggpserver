<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	
	<!--  <xsl:key name="by-fluent" match="/match/state/fact" use="prop-f" />  -->
	
	<xsl:template name="legalMove">
		<xsl:param name="url"/> <!-- the URL to propose this move (no confirm) -->
		<xsl:param name="urlWithConfirm"/> <!-- the URL to propose this move (with confirm of this move) -->
		
		<xsl:if test="./move-term/prop-f = 'DROP'">
			<script language="JavaScript" type="text/javascript">
				<xsl:text disable-output-escaping="yes">address[</xsl:text>
				<xsl:value-of select="./move-term/arg[1]"/> <!-- x-coordinate -->
				<xsl:text disable-output-escaping="yes">] = "</xsl:text>
				<xsl:value-of select="$urlWithConfirm" />
				<xsl:text disable-output-escaping="yes">";</xsl:text>
			</script>
		</xsl:if>
		
	</xsl:template>
	
	
	<xsl:template name="linkOnCell">
		<xsl:param name="x" />
		<xsl:param name="y" />
		<xsl:param name="content" />
		<xsl:param name="piece" />
		
		<xsl:choose>
			<xsl:when test="count(/match/state/fact[prop-f='CELL' and arg[1]=$x and arg[3] != 'B']) = $y">
				1
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<xsl:template name="print_state">
		
		<script language="JavaScript" type="text/javascript">
			<![CDATA[
				<!--
					var address = new Array();
				-->
			]]>
		</script>
		
		<script language="JavaScript" type="text/javascript">
			<![CDATA[
				<!--
				function callBack (x, y, content, piece) {
					//alert("<"+address[x]+">");
					if (address[x] != undefined && address[x] != "") {
						location.replace(address[x]);
					}
				}
				-->
			]]>
		</script>
		
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="checkered" select="'no'"/>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
			<xsl:with-param name="cellCallBack">
				<xsl:if test="count(/match/legalmoves) = 1">
					callBack
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	
	<xsl:template name="make_cell_content">
		<xsl:param name="piece"/>
		<xsl:param name="content"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:variable name="piece2">
			<xsl:choose>
				<xsl:when test="$content='DIRT'">xx</xsl:when>
				<xsl:when test="$content='R'">O2</xsl:when>
				<xsl:when test="$content='W'">O1</xsl:when>
				<xsl:otherwise><xsl:value-of select="$piece"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="background2">
			<xsl:choose>
				<xsl:when test="$content='DIRT'">dark</xsl:when>
				<xsl:otherwise><xsl:value-of select="$background"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece2"/>
			<xsl:with-param name="background" select="$background2"/>
			<xsl:with-param name="alt" select="$alt"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>