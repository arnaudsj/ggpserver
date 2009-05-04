<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	asteroids
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">

		<style type="text/css" media="all">
			div.asteroidsBoard {
				position:relative;
			}
			div.asteroidsShip {
				position:absolute;
				height:16px;
				width:16px;
				background-color: #000000;
			}
			div.asteroidsVelocity {
				position:absolute;
				background-color: #333333;
			}
			div.asteroidsHeading {
				position:absolute;
				background-color: #FF0000;
			}
		</style>
		
		<div class="asteroidsBoard">
			<!-- draw board (with planet) -->
			<xsl:call-template name="board">
				<xsl:with-param name="Width">20</xsl:with-param>
				<xsl:with-param name="Height">20</xsl:with-param>
				<xsl:with-param name="CellWidth">20</xsl:with-param>
				<xsl:with-param name="checkered">no</xsl:with-param>
				<xsl:with-param name="LightCellColor">#CCCCCC</xsl:with-param>
				<xsl:with-param name="DefaultCell">no</xsl:with-param>
			</xsl:call-template>
			
			<!-- Grab Ship Data -->
			<xsl:variable name="xShip" select="2 + (fact[prop-f='X']/arg[1] - 1) * 20"/>
			<xsl:variable name="yShip" select="2 + (20 - fact[prop-f='Y']/arg[1]) * 20"/>
	
			<!-- Draw ship -->
			<div class="asteroidsShip">
				<xsl:attribute name="style">
					left: <xsl:value-of select="$xShip"/>px;
					top:  <xsl:value-of select="$yShip"/>px;
				</xsl:attribute>
			</div>
			
			<!-- Draw heading -->
			<div class="asteroidsHeading">
				<xsl:attribute name="style">
					<xsl:choose>
						<xsl:when test="fact[prop-f='HEADING']/arg[1]='NORTH'">
							top: <xsl:value-of select="$yShip - 11"/>px;
							left: <xsl:value-of select="$xShip + 6"/>px;
							height: 20px;
							width:  4px;
						</xsl:when>
						<xsl:when test="fact[prop-f='HEADING']/arg[1]='SOUTH'">
							top: <xsl:value-of select="$yShip + 9"/>px;
							left: <xsl:value-of select="$xShip + 6"/>px;
							height: 20px;
							width:  4px;
						</xsl:when>
						<xsl:when test="fact[prop-f='HEADING']/arg[1]='EAST'">
							top: <xsl:value-of select="$yShip + 6"/>px;
							left: <xsl:value-of select="$xShip + 9"/>px;
							height: 4px;
							width:  20px;
						</xsl:when>
						<xsl:when test="fact[prop-f='HEADING']/arg[1]='WEST'">
							top: <xsl:value-of select="$yShip + 6"/>px;
							left: <xsl:value-of select="$xShip - 11"/>px;
							height: 4px;
							width:  20px;
						</xsl:when>
					</xsl:choose>
				</xsl:attribute>
			</div>
			
			<!-- Draw north-south velocity -->				
			<xsl:variable name="deltaY" select="number(fact[prop-f='NORTH-SPEED']/arg[1])"/>
			<xsl:if test="$deltaY != 0">
				<xsl:variable name="yVectorLen" select="number(translate(string($deltaY * 20 + 1),'-',''))"/>
				<div class="asteroidsVelocity">
					<xsl:attribute name="style">
						left:   <xsl:value-of select="$xShip + 7"/>px;
						<xsl:choose>
							<xsl:when test="$deltaY &lt; 0">top: <xsl:value-of select="$yShip + 9"/>px;</xsl:when>
							<xsl:otherwise>top: <xsl:value-of select="$yShip + 9 - $yVectorLen"/>px;</xsl:otherwise>
						</xsl:choose>
						height: <xsl:value-of select="$yVectorLen"/>px;
						width:  2px;
					</xsl:attribute>
				</div>
			</xsl:if>
			
			<!-- Draw east-west velocity -->				
			<xsl:variable name="deltaX" select="number(fact[prop-f='EAST-SPEED']/arg[1])"/>
			<xsl:if test="$deltaX != 0">
				<xsl:variable name="xVectorLen" select="number(translate(string($deltaX * 20 + 1),'-',''))"/>
				<div class="asteroidsVelocity">
					<xsl:attribute name="style">
						top:   <xsl:value-of select="$yShip + 7"/>px;
						<xsl:choose>
							<xsl:when test="$deltaX &lt; 0">left: <xsl:value-of select="$xShip + 9 - $xVectorLen"/>px;</xsl:when>
							<xsl:otherwise>left: <xsl:value-of select="$xShip + 9"/>px;</xsl:otherwise>
						</xsl:choose>
						width: <xsl:value-of select="$xVectorLen"/>px;
						height:  2px;
					</xsl:attribute>
				</div>
			</xsl:if>
		</div>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'HEADING'"/>
			<xsl:with-param name="excludeFluent2" select="'X'"/>
			<xsl:with-param name="excludeFluent3" select="'Y'"/>
			<xsl:with-param name="excludeFluent4" select="'NORTH-SPEED'"/>
			<xsl:with-param name="excludeFluent5" select="'EAST-SPEED'"/>
		</xsl:call-template>
		
	</xsl:template>
	
	<xsl:template name="make_cell">
		<xsl:param name="col"/>
		<xsl:param name="row"/>
		<xsl:param name="defaultClass"/>
		<div>
			<xsl:attribute name="class"><xsl:value-of select="$defaultClass"/></xsl:attribute>
			<xsl:if test="$col=15 and $row=5">
				<xsl:attribute name="style">
					background-color: #99CCFF;
				</xsl:attribute>
			</xsl:if>
		</div>
	</xsl:template>
	
</xsl:stylesheet>

