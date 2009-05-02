<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for printing a rectangular board of fixed size
	- For use within <body>.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="board">

		<xsl:param name="Width"/> <!-- the number of cells per column -->
		<xsl:param name="Height"/> <!-- the number of cells per row -->
		<xsl:param name="CellWidth">40</xsl:param> <!-- the width of each cell in px -->
		<xsl:param name="CellHeight" select="$CellWidth"/> <!-- the height of each cell in px -->
		<xsl:param name="CellFluentName">CELL</xsl:param>
		<xsl:param name="checkered">no</xsl:param>
			<!-- no - all cells have light background
			      light - the first cell has light background
			      dark - the first cell has dark background -->
		<xsl:param name="LightCellColor">#CCCCCC</xsl:param>
		<xsl:param name="DarkCellColor">#AAAAAA</xsl:param>

		<style type="text/css" media="all">

			div.board
			{
				width:  <xsl:value-of select="$Width*$CellWidth"/>px;
				height: <xsl:value-of select="$Height*$CellHeight"/>px;
			}
			div.cellLight
			{
				width:  <xsl:value-of select="$CellWidth - 4"/>px;
				height: <xsl:value-of select="$CellHeight - 4"/>px;
				float:	left;
				border: 2px solid #FFC;
				background-color: <xsl:value-of select="$LightCellColor"/>;
			}
			div.cellDark
			{
				width:  <xsl:value-of select="$CellWidth - 4"/>px;
				height: <xsl:value-of select="$CellHeight - 4"/>px;
				float:	left;
				border: 2px solid #FFC;
				background-color: <xsl:value-of select="$DarkCellColor"/>;
			}

		</style>

		<div class="board">
			<xsl:call-template name="row-loop">
				<xsl:with-param name="width" select="$Width"/>
				<xsl:with-param name="height" select="$Height"/>
				<xsl:with-param name="checkered" select="$checkered"/>
			</xsl:call-template>
		</div>

	</xsl:template>

	<xsl:template name="row-loop">
		<xsl:param name="row">1</xsl:param>
		<xsl:param name="width"/>
		<xsl:param name="height"/>
		<xsl:param name="checkered"/>

		<xsl:call-template name="col-loop">
			<xsl:with-param name="row" select="$row"/>
			<xsl:with-param name="width" select="$width"/>
			<xsl:with-param name="checkered" select="$checkered"/>
		</xsl:call-template>
		<br/>

		<!-- loop -->
		<xsl:if test="$row &lt; $height">
			<xsl:call-template name="row-loop">
				<xsl:with-param name="row" select="$row + 1"/>
				<xsl:with-param name="width" select="$width"/>
				<xsl:with-param name="height" select="$height"/>
				<xsl:with-param name="checkered" select="$checkered"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="col-loop">
		<xsl:param name="col">1</xsl:param>
		<xsl:param name="row"/>
		<xsl:param name="width"/>
		<xsl:param name="checkered"/>

		<div>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="($checkered='dark' and (($col mod 2) + ($row mod 2) != 1)) or ($checkered='light' and (($col mod 2) + ($row mod 2) = 1))">
						cellDark
					</xsl:when>
					<xsl:otherwise>
						cellLight
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</div>

		<!-- loop -->
		<xsl:if test="$col &lt; $width">
			<xsl:call-template name="col-loop">
				<xsl:with-param name="col" select="$col + 1"/>
				<xsl:with-param name="row" select="$row"/>
				<xsl:with-param name="width" select="$width"/>
				<xsl:with-param name="checkered" select="$checkered"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>