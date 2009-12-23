<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	asteroids
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_asteroids_universes"/>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state"/>
	</xsl:template>
	
	<xsl:template name="print_asteroids_universes">
		<xsl:for-each select="fact">
			<xsl:sort select="prop-f"/>
			<!--<xsl:value-of select="prop-f"/> - <xsl:value-of select="preceding-sibling::fact[1]/prop-f"/><br/>-->
			<xsl:if test="not(prop-f = preceding-sibling::fact[1]/prop-f)">
				<xsl:if test="starts-with(prop-f, 'NORTH-SPEED')">
					<xsl:variable name="ID" select="substring-after(prop-f, 'NORTH-SPEED')"/>
					<xsl:if test="../fact[prop-f=concat('EAST-SPEED',$ID)] and ../fact[prop-f=concat('HEADING',$ID)] and ../fact[prop-f=concat('X',$ID)] and ../fact[prop-f=concat('Y',$ID)]">
						<xsl:for-each select="..">
							<xsl:call-template name="print_asteroids_universe">
								<xsl:with-param name="ID" select="$ID"/>
							</xsl:call-template>
						</xsl:for-each>
					</xsl:if>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="print_asteroids_universe">
		<xsl:param name="ID"/>
		<xsl:param name="shipXFluent" select="concat('X',$ID)"/>
		<xsl:param name="shipYFluent" select="concat('Y',$ID)"/>
		<xsl:param name="headingFluent" select="concat('HEADING',$ID)"/>
		<xsl:param name="northSpeedFluent" select="concat('NORTH-SPEED',$ID)"/>
		<xsl:param name="eastSpeedFluent" select="concat('EAST-SPEED',$ID)"/>

		
		<style type="text/css" media="all">
			div.asteroidsUniverse {
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
		
		<div class="asteroidsUniverse">
			<!-- draw board (with planet) -->
			<xsl:call-template name="board">
				<xsl:with-param name="Width">20</xsl:with-param>
				<xsl:with-param name="Height">20</xsl:with-param>
				<xsl:with-param name="CellWidth">20</xsl:with-param>
				<xsl:with-param name="checkered">no</xsl:with-param>
				<xsl:with-param name="LightCellColor">#CCCCCC</xsl:with-param>
				<xsl:with-param name="DefaultCell">no</xsl:with-param>
			</xsl:call-template>
			<p style="text-align:center;">Universe <xsl:value-of select="$ID"/></p>
			<xsl:comment>
				<xsl:value-of select="$shipXFluent"/>, <xsl:value-of select="$shipYFluent"/>, <xsl:value-of select="$headingFluent"/>, <xsl:value-of select="$northSpeedFluent"/>, <xsl:value-of select="$eastSpeedFluent"/>
			</xsl:comment>
			
			<!-- Grab Ship Data -->
			<xsl:variable name="xShip" select="2 + (fact[prop-f=$shipXFluent]/arg[1] - 1) * 20"/>
			<xsl:variable name="yShip" select="2 + (20 - fact[prop-f=$shipYFluent]/arg[1]) * 20"/>
	
			<!-- Draw ship -->
			<div class="asteroidsShip" title="ship">
				<xsl:attribute name="style">
					left: <xsl:value-of select="$xShip"/>px;
					top:  <xsl:value-of select="$yShip"/>px;
				</xsl:attribute>
			</div>
			
			<!-- Draw heading -->
			<div class="asteroidsHeading">
				<xsl:attribute name="style">
					<xsl:variable name="heading" select="fact[prop-f=$headingFluent]/arg[1]"/>
					<xsl:choose>
						<xsl:when test="contains($heading,'NORTH')">
							top: <xsl:value-of select="$yShip - 11"/>px;
							left: <xsl:value-of select="$xShip + 6"/>px;
							height: 20px;
							width:  4px;
						</xsl:when>
						<xsl:when test="contains($heading,'SOUTH')">
							top: <xsl:value-of select="$yShip + 9"/>px;
							left: <xsl:value-of select="$xShip + 6"/>px;
							height: 20px;
							width:  4px;
						</xsl:when>
						<xsl:when test="contains($heading,'EAST')">
							top: <xsl:value-of select="$yShip + 6"/>px;
							left: <xsl:value-of select="$xShip + 9"/>px;
							height: 4px;
							width:  20px;
						</xsl:when>
						<xsl:when test="contains($heading,'WEST')">
							top: <xsl:value-of select="$yShip + 6"/>px;
							left: <xsl:value-of select="$xShip - 11"/>px;
							height: 4px;
							width:  20px;
						</xsl:when>
					</xsl:choose>
				</xsl:attribute>
			</div>
			
			<!-- Draw north-south velocity -->				
			<xsl:variable name="deltaY" select="number(fact[prop-f=$northSpeedFluent]/arg[1])"/>
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
			<xsl:variable name="deltaX" select="number(fact[prop-f=$eastSpeedFluent]/arg[1])"/>
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
				<xsl:attribute name="title">planet</xsl:attribute>
			</xsl:if>
		</div>
	</xsl:template>
	
</xsl:stylesheet>

