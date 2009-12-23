<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Cubicup
	
	TODO: detect size automatically
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
				
		<style type="text/css" media="all">
			.board{
				position: relative;
				width:    320px;
				height:   320px;
				padding:  0px;
			}
			.cube{
				position: absolute;
				height:   57px;
				width:    39px;
				background-color: transparent;
			}
		</style>

		<div class="board">
			<xsl:for-each select="fact[prop-f='CUBE']">
				<xsl:sort select="arg[1]" order="ascending"/>
				<xsl:sort select="arg[3]" order="ascending"/>
				<xsl:sort select="arg[2]" order="ascending"/>

				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				
				<xsl:variable name="zCoord" select="arg[3]"/>
				<xsl:variable name="yCoord" select="arg[1]"/>
				<xsl:variable name="xCoord" select="arg[2]"/>

				<xsl:variable name="yCell" select="$yCoord * 37 - $zCoord * 19"/>
				<xsl:variable name="xCell" select="$xCoord * 38 + (7 - $yCoord) * 19 - $zCoord*19"/>
				
				<div class="cube">
					<xsl:attribute name="style">
						<xsl:value-of select="concat('left:', $xCell ,'px; top:', $yCell ,'px;')"/>
					</xsl:attribute>
					
					<img width="39px" height="57px">
						<xsl:attribute name="src">
							<xsl:value-of select="$stylesheetURL"/>
							<xsl:text>/cubicup/</xsl:text>
							<xsl:choose>
								<xsl:when test="arg[4]='BASE'">
									<xsl:text>gray</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:variable name="up" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
									<xsl:variable name="lo" select="'abcdefghijklmnopqrstuvwxyz'"/>
									<xsl:value-of select="translate(arg[4],$up,$lo)"/>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:text>_cube.png</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
						<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
					</img>
				</div>
			
			</xsl:for-each>
		</div>

		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'CUBE'"/>
		</xsl:call-template>
	
	</xsl:template>

</xsl:stylesheet>
