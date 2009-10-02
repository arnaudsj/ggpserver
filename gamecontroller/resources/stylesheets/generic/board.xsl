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
		<xsl:param name="BorderWidth">2</xsl:param> <!-- the width of the boarder around each cell in px -->
		<xsl:param name="BorderStyle">solid #FFC</xsl:param>
		<xsl:param name="CellFluentName">CELL</xsl:param>
		<xsl:param name="checkered">no</xsl:param>
			<!-- no - all cells have light background
			      light - the first cell has light background
			      dark - the first cell has dark background
			      alldark - all cells have dark background
			      invisible - all cells are invisible -->
		<xsl:param name="LightCellColor">#CCCCCC</xsl:param>
		<xsl:param name="DarkCellColor">#AAAAAA</xsl:param>
		<xsl:param name="DefaultCell">yes</xsl:param>
			<!-- if "no" needs a template named make_cell_class with numeric arguments "col", "row", "defaultClass" that has to print
					a left-floating div of size CellWidth x CellHeight (default css classes are cellLight, cellDark, cellInvisible) -->

		<style type="text/css" media="all">

			div.board
			{
				width:  <xsl:value-of select="$Width*$CellWidth"/>px;
				height: <xsl:value-of select="$Height*$CellHeight"/>px;
			}
			div.cellLight
			{
				width:  <xsl:value-of select="$CellWidth - 2 * $BorderWidth"/>px;
				height: <xsl:value-of select="$CellHeight - 2 * $BorderWidth"/>px;
				float:	left;
				<xsl:if test="$BorderWidth>0">
					border: <xsl:value-of select="$BorderWidth"/>px <xsl:value-of select="$BorderStyle"/>;
				</xsl:if>
				background-color: <xsl:value-of select="$LightCellColor"/>;
			}
			div.cellDark
			{
				width:  <xsl:value-of select="$CellWidth - 2 * $BorderWidth"/>px;
				height: <xsl:value-of select="$CellHeight - 2 * $BorderWidth"/>px;
				float:	left;
				<xsl:if test="$BorderWidth>0">
					border: <xsl:value-of select="$BorderWidth"/>px <xsl:value-of select="$BorderStyle"/>;
				</xsl:if>
				background-color: <xsl:value-of select="$DarkCellColor"/>;
			}
			div.cellInvisible
			{
				width:  <xsl:value-of select="$CellWidth - 2 * $BorderWidth"/>px;
				height: <xsl:value-of select="$CellHeight - 2 * $BorderWidth"/>px;
				float:	left;
				<xsl:if test="$BorderWidth>0">
					border: <xsl:value-of select="$BorderWidth"/>px <xsl:value-of select="$BorderStyle"/>;
				</xsl:if>
			}

		</style>

		<div class="board">
			<xsl:call-template name="row-loop">
				<xsl:with-param name="width" select="$Width"/>
				<xsl:with-param name="height" select="$Height"/>
				<xsl:with-param name="checkered" select="$checkered"/>
				<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
			</xsl:call-template>
		</div>

	</xsl:template>

	<xsl:template name="row-loop">
		<xsl:param name="width"/>
		<xsl:param name="height"/>
		<xsl:param name="checkered"/>
		<xsl:param name="DefaultCell"/>
		<xsl:param name="row" select="$height"/>

		<xsl:call-template name="col-loop">
			<xsl:with-param name="row" select="$row"/>
			<xsl:with-param name="width" select="$width"/>
			<xsl:with-param name="height" select="$height"/>
			<xsl:with-param name="checkered" select="$checkered"/>
			<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
		</xsl:call-template>

		<!-- loop -->
		<xsl:if test="$row &gt; 1">
			<xsl:call-template name="row-loop">
				<xsl:with-param name="row" select="$row - 1"/>
				<xsl:with-param name="width" select="$width"/>
				<xsl:with-param name="height" select="$height"/>
				<xsl:with-param name="checkered" select="$checkered"/>
				<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="col-loop">
		<xsl:param name="col">1</xsl:param>
		<xsl:param name="row"/>
		<xsl:param name="width"/>
		<xsl:param name="height"/>
		<xsl:param name="checkered"/>
		<xsl:param name="DefaultCell"/>

		<xsl:variable name="defaultClass">
			<xsl:choose>
				<xsl:when test="($checkered='dark' and (($col mod 2) + (($height + 1 - $row) mod 2) != 1)) or ($checkered='light' and (($col mod 2) + (($height + 1 - $row) mod 2) = 1))">cellDark</xsl:when>
				<xsl:when test="$checkered='alldark'">cellDark</xsl:when>
				<xsl:when test="$checkered='invisible'">cellInvisible</xsl:when>
				<xsl:otherwise>cellLight</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$DefaultCell='yes'">
				<div>
					<xsl:attribute name="class">
						<xsl:value-of select="$defaultClass"/>
					</xsl:attribute>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="make_cell">
					<xsl:with-param name="col" select="$col"/>
					<xsl:with-param name="row" select="$row"/>
					<xsl:with-param name="defaultClass" select="$defaultClass"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		
		<!-- loop -->
		<xsl:if test="$col &lt; $width">
			<xsl:call-template name="col-loop">
				<xsl:with-param name="col" select="$col + 1"/>
				<xsl:with-param name="row" select="$row"/>
				<xsl:with-param name="width" select="$width"/>
				<xsl:with-param name="height" select="$height"/>
				<xsl:with-param name="checkered" select="$checkered"/>
				<xsl:with-param name="DefaultCell" select="$DefaultCell"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
