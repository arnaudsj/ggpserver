<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	racer
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_race_track"/>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'POSX'"/>
			<xsl:with-param name="excludeFluent2" select="'POSY'"/>
			<xsl:with-param name="excludeFluent3" select="'VELX'"/>
			<xsl:with-param name="excludeFluent4" select="'VELY'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="print_race_track">
		<style type="text/css" media="all">
			div.race_track {
				position:relative;
			}
			div.racerCar {
				position:absolute;
				height:12px;
				width:12px;
			}
			div.carVelocity {
				position:absolute;
			}
		</style>
		
		<div class="race_track">
			<!-- track -->
			<xsl:call-template name="board">
				<xsl:with-param name="Width">25</xsl:with-param>
				<xsl:with-param name="Height">22</xsl:with-param>
				<xsl:with-param name="CellWidth">20</xsl:with-param>
				<xsl:with-param name="BorderWidth">1</xsl:with-param>
				<xsl:with-param name="checkered">no</xsl:with-param>
				<xsl:with-param name="LightCellColor">#EEEEEE</xsl:with-param>
				<xsl:with-param name="DefaultCell">no</xsl:with-param>
			</xsl:call-template>
			
			<xsl:for-each select="fact[prop-f='POSX']">
				<xsl:sort select="arg[1]" order="descending"/>
				<xsl:variable name="player" select="arg[1]"/>
				<xsl:variable name="posx">
					<xsl:call-template name="constant_to_number">
						<xsl:with-param name="constant" select="arg[2]"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="posy">
					<xsl:call-template name="constant_to_number">
						<xsl:with-param name="constant" select="../fact[prop-f='POSY' and arg[1]=$player]/arg[2]"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="velx">
					<xsl:call-template name="constant_to_number">
						<xsl:with-param name="constant" select="../fact[prop-f='VELX' and arg[1]=$player]/arg[2]"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="vely">
					<xsl:call-template name="constant_to_number">
						<xsl:with-param name="constant" select="../fact[prop-f='VELY' and arg[1]=$player]/arg[2]"/>
					</xsl:call-template>
				</xsl:variable>
				
				<xsl:comment>
					posx:<xsl:value-of select="$posx"/>(<xsl:value-of select="arg[2]"/>)
					posy:<xsl:value-of select="$posy"/>(<xsl:value-of select="../fact[prop-f='POSY' and arg[1]=$player]/arg[2]"/>)
					velx:<xsl:value-of select="$velx"/>(<xsl:value-of select="../fact[prop-f='VELX' and arg[1]=$player]/arg[2]"/>)
					vely:<xsl:value-of select="$vely"/>(<xsl:value-of select="../fact[prop-f='VELY' and arg[1]=$player]/arg[2]"/>)
				</xsl:comment>
				
				<xsl:variable name="xCar1" select="1 + $posx * 20"/>
				<xsl:variable name="yCar1" select="1 + (22 - ($posy + 5)) * 20"/>
				<xsl:variable name="xCar">
					<xsl:choose>
						<xsl:when test="$player='YELLOW' or $player='RED'">
							<xsl:value-of select="$xCar1"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$xCar1 + 6"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="yCar">
					<xsl:choose>
						<xsl:when test="$player='GREEN' or $player='YELLOW'">
							<xsl:value-of select="$yCar1"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$yCar1 + 6"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<!-- Draw car -->
				<div class="racerCar">
					<xsl:attribute name="style">
						left: <xsl:value-of select="$xCar"/>px;
						top:  <xsl:value-of select="$yCar"/>px;
						background-color: <xsl:value-of select="$player"/>;
					</xsl:attribute>
					<xsl:attribute name="title">
						<xsl:value-of select="$player"/>
							(x:<xsl:value-of select="$posx"/>,
							 y:<xsl:value-of select="$posy"/>,
							 velx:<xsl:value-of select="$velx"/>,
							 vely:<xsl:value-of select="$vely"/>)
					</xsl:attribute>
				</div>
			
				<!-- Draw north-south velocity -->				
				<xsl:variable name="deltaY" select="number($vely)"/>
				<xsl:if test="$deltaY != 0">
					<xsl:variable name="yVectorLen" select="number(translate(string($deltaY * 20 + 1),'-',''))"/>
					<div class="carVelocity">
						<xsl:attribute name="style">
							left:   <xsl:value-of select="$xCar + 5"/>px;
							<xsl:choose>
								<xsl:when test="$deltaY &lt; 0">top: <xsl:value-of select="$yCar + 7"/>px;</xsl:when>
								<xsl:otherwise>top: <xsl:value-of select="$yCar + 7 - $yVectorLen"/>px;</xsl:otherwise>
							</xsl:choose>
							height: <xsl:value-of select="$yVectorLen"/>px;
							width:  2px;
							background-color: <xsl:value-of select="$player"/>;
						</xsl:attribute>
					</div>
				</xsl:if>
				
				<!-- Draw east-west velocity -->				
				<xsl:variable name="deltaX" select="number($velx)"/>
				<xsl:if test="$deltaX != 0">
					<xsl:variable name="xVectorLen" select="number(translate(string($deltaX * 20 + 1),'-',''))"/>
					<div class="carVelocity">
						<xsl:attribute name="style">
							top:   <xsl:value-of select="$yCar + 5"/>px;
							<xsl:choose>
								<xsl:when test="$deltaX &lt; 0">left: <xsl:value-of select="$xCar + 7 - $xVectorLen"/>px;</xsl:when>
								<xsl:otherwise>left: <xsl:value-of select="$xCar + 7"/>px;</xsl:otherwise>
							</xsl:choose>
							width: <xsl:value-of select="$xVectorLen"/>px;
							height:  2px;
							background-color: <xsl:value-of select="$player"/>;
						</xsl:attribute>
					</div>
				</xsl:if> 
			</xsl:for-each>
		</div>
	</xsl:template>
	
	<xsl:template name="make_cell">
		<xsl:param name="col"/>
		<xsl:param name="row"/>
		<xsl:param name="defaultClass"/>
		<div>
			<xsl:attribute name="class"><xsl:value-of select="$defaultClass"/></xsl:attribute>
			<xsl:choose>
				<xsl:when test="
							($col=1 and $row=9) or
							($col=1 and $row=10) or
							($col=1 and $row=11) or
							($col=1 and $row=12) or
							($col=2 and $row=5) or
							($col=2 and $row=6) or
							($col=2 and $row=7) or
							($col=2 and $row=8) or
							($col=2 and $row=9) or
							($col=2 and $row=10) or
							($col=2 and $row=11) or
							($col=2 and $row=12) or
							($col=2 and $row=13) or
							($col=3 and $row=5) or
							($col=3 and $row=6) or
							($col=3 and $row=7) or
							($col=3 and $row=8) or
							($col=3 and $row=9) or
							($col=3 and $row=10) or
							($col=3 and $row=11) or
							($col=3 and $row=12) or
							($col=3 and $row=13) or
							($col=3 and $row=14) or
							($col=3 and $row=15) or
							($col=4 and $row=5) or
							($col=4 and $row=6) or
							($col=4 and $row=7) or
							($col=4 and $row=8) or
							($col=4 and $row=9) or
							($col=4 and $row=10) or
							($col=4 and $row=11) or
							($col=4 and $row=12) or
							($col=4 and $row=13) or
							($col=4 and $row=14) or
							($col=4 and $row=15) or
							($col=4 and $row=16) or
							($col=4 and $row=17) or
							($col=5 and $row=5) or
							($col=5 and $row=6) or
							($col=5 and $row=7) or
							($col=5 and $row=8) or
							($col=5 and $row=9) or
							($col=5 and $row=10) or
							($col=5 and $row=11) or
							($col=5 and $row=12) or
							($col=5 and $row=13) or
							($col=5 and $row=14) or
							($col=5 and $row=15) or
							($col=5 and $row=16) or
							($col=5 and $row=17) or
							($col=5 and $row=18) or
							($col=6 and $row=11) or
							($col=6 and $row=12) or
							($col=6 and $row=13) or
							($col=6 and $row=14) or
							($col=6 and $row=15) or
							($col=6 and $row=16) or
							($col=6 and $row=17) or
							($col=6 and $row=18) or
							($col=6 and $row=19) or
							($col=7 and $row=12) or
							($col=7 and $row=13) or
							($col=7 and $row=14) or
							($col=7 and $row=15) or
							($col=7 and $row=16) or
							($col=7 and $row=17) or
							($col=7 and $row=18) or
							($col=7 and $row=19) or
							($col=7 and $row=20) or
							($col=8 and $row=13) or
							($col=8 and $row=14) or
							($col=8 and $row=15) or
							($col=8 and $row=16) or
							($col=8 and $row=17) or
							($col=8 and $row=18) or
							($col=8 and $row=19) or
							($col=8 and $row=20) or
							($col=8 and $row=21) or
							($col=9 and $row=15) or
							($col=9 and $row=16) or
							($col=9 and $row=17) or
							($col=9 and $row=18) or
							($col=9 and $row=19) or
							($col=9 and $row=20) or
							($col=9 and $row=21) or
							($col=9 and $row=22) or
							($col=10 and $row=15) or
							($col=10 and $row=16) or
							($col=10 and $row=17) or
							($col=10 and $row=18) or
							($col=10 and $row=19) or
							($col=10 and $row=20) or
							($col=10 and $row=21) or
							($col=10 and $row=22) or
							($col=11 and $row=12) or
							($col=11 and $row=13) or
							($col=11 and $row=14) or
							($col=11 and $row=15) or
							($col=11 and $row=16) or
							($col=11 and $row=17) or
							($col=11 and $row=18) or
							($col=11 and $row=19) or
							($col=11 and $row=20) or
							($col=11 and $row=21) or
							($col=12 and $row=12) or
							($col=12 and $row=13) or
							($col=12 and $row=14) or
							($col=12 and $row=15) or
							($col=12 and $row=16) or
							($col=12 and $row=17) or
							($col=12 and $row=18) or
							($col=12 and $row=19) or
							($col=12 and $row=20) or
							($col=13 and $row=11) or
							($col=13 and $row=12) or
							($col=13 and $row=13) or
							($col=13 and $row=14) or
							($col=13 and $row=15) or
							($col=13 and $row=16) or
							($col=14 and $row=11) or
							($col=14 and $row=12) or
							($col=14 and $row=13) or
							($col=14 and $row=14) or
							($col=14 and $row=15) or
							($col=15 and $row=11) or
							($col=15 and $row=12) or
							($col=15 and $row=13) or
							($col=15 and $row=14) or
							($col=15 and $row=15) or
							($col=16 and $row=10) or
							($col=16 and $row=11) or
							($col=16 and $row=12) or
							($col=16 and $row=13) or
							($col=16 and $row=14) or
							($col=16 and $row=15) or
							($col=17 and $row=10) or
							($col=17 and $row=11) or
							($col=17 and $row=12) or
							($col=17 and $row=13) or
							($col=17 and $row=14) or
							($col=17 and $row=15) or
							($col=18 and $row=11) or
							($col=18 and $row=12) or
							($col=18 and $row=13) or
							($col=18 and $row=14) or
							($col=18 and $row=15) or
							($col=18 and $row=16) or
							($col=19 and $row=12) or
							($col=19 and $row=13) or
							($col=19 and $row=14) or
							($col=19 and $row=15) or
							($col=19 and $row=16) or
							($col=20 and $row=13) or
							($col=20 and $row=14) or
							($col=20 and $row=15) or
							($col=20 and $row=16) or
							($col=20 and $row=17) or
							($col=21 and $row=13) or
							($col=21 and $row=14) or
							($col=21 and $row=15) or
							($col=21 and $row=16) or
							($col=21 and $row=17) or
							($col=21 and $row=18) or
							($col=22 and $row=5) or
							($col=22 and $row=6) or
							($col=22 and $row=7) or
							($col=22 and $row=8) or
							($col=22 and $row=9) or
							($col=22 and $row=10) or
							($col=22 and $row=11) or
							($col=22 and $row=12) or
							($col=22 and $row=13) or
							($col=22 and $row=14) or
							($col=22 and $row=15) or
							($col=22 and $row=16) or
							($col=22 and $row=17) or
							($col=22 and $row=18) or
							($col=23 and $row=5) or
							($col=23 and $row=6) or
							($col=23 and $row=7) or
							($col=23 and $row=8) or
							($col=23 and $row=9) or
							($col=23 and $row=10) or
							($col=23 and $row=11) or
							($col=23 and $row=12) or
							($col=23 and $row=13) or
							($col=23 and $row=14) or
							($col=23 and $row=15) or
							($col=23 and $row=16) or
							($col=23 and $row=17) or
							($col=24 and $row=5) or
							($col=24 and $row=6) or
							($col=24 and $row=7) or
							($col=24 and $row=8) or
							($col=24 and $row=9) or
							($col=24 and $row=10) or
							($col=24 and $row=11) or
							($col=24 and $row=12) or
							($col=24 and $row=13) or
							($col=24 and $row=14) or
							($col=24 and $row=15) or
							($col=24 and $row=16) or
							($col=25 and $row=5) or
							($col=25 and $row=6) or
							($col=25 and $row=7) or
							($col=25 and $row=8) or
							($col=25 and $row=9) or
							($col=25 and $row=10) or
							($col=25 and $row=11) or
							($col=25 and $row=12) or
							($col=25 and $row=13) or
							($col=25 and $row=14) or
							($col=25 and $row=15)">
					<xsl:attribute name="style">
						background-color: #000000;
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="
						($col=22 and $row=1) or
						($col=22 and $row=2) or
						($col=22 and $row=3) or
						($col=22 and $row=4) or
						($col=23 and $row=1) or
						($col=23 and $row=2) or
						($col=23 and $row=3) or
						($col=23 and $row=4) or
						($col=24 and $row=1) or
						($col=24 and $row=2) or
						($col=24 and $row=3) or
						($col=24 and $row=4) or
						($col=25 and $row=1) or
						($col=25 and $row=2) or
						($col=25 and $row=3) or
						($col=25 and $row=4)
						">
					<xsl:attribute name="style">
						background-color: #999999;
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>
	
	<xsl:template name="constant_to_number">
		<xsl:param name="constant"/>
		<xsl:value-of select="number(translate(translate($constant, 'N', '-'), 'P', ''))"/>
	</xsl:template>

</xsl:stylesheet>

