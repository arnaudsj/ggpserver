<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="Width" select="'3'"/>
			<xsl:with-param name="Height" select="'3'"/>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
			<xsl:with-param name="checkered">alldark</xsl:with-param>
			<xsl:with-param name="CellFluentName">CELL</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="alt"/>
		<!-- with a little abuse of the chess templates we just ignore pieces and background information and use the arguments of the fluent directly -->
		<xsl:variable name="value" select="arg[3]"/>
		<xsl:variable name="color" select="translate(arg[4], 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
		
		<img>
			<xsl:attribute name="width">44</xsl:attribute>
			<xsl:attribute name="height">44</xsl:attribute>
			<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
			<xsl:attribute name="src">
				<xsl:value-of select="$stylesheetURL"/>
				<xsl:text>/generic/dice_images/die_</xsl:text>
				<xsl:value-of select="$color"/>
				<xsl:text>_</xsl:text>
				<xsl:value-of select="$value"/>
				<xsl:text>.png</xsl:text>
			</xsl:attribute>
		</img>
	</xsl:template>

</xsl:stylesheet>
