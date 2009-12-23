<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Hitori
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>

	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
			<xsl:with-param name="CellFluentName" select="'CELL'"/>
			<xsl:with-param name="checkered" select="'no'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="make_cell_content">
		<xsl:param name="alt"/>
		<div>
			<xsl:variable name="col" select="./arg[1]"/>
			<xsl:variable name="row" select="./arg[2]"/>
			<xsl:variable name="value" select="./arg[3]"/>
			<xsl:variable name="mark" select="./arg[4]"/>
			<xsl:attribute name="style">
				width:100%;
				height:100%;
				text-align: center;
				font-size: 30px;
				<xsl:choose>
					<xsl:when test="$mark='B'">
						background-color:#808080;
					</xsl:when>
					<xsl:when test="../fact[prop-f='CELL' and ((arg[1]=$col and arg[2]!=$row) or (arg[1]!=$col and arg[2]=$row)) and arg[3]=$value and arg[4]!='B']">
						color:red;
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
			<xsl:value-of select="$value"/>
		</div>
	</xsl:template>

</xsl:stylesheet>