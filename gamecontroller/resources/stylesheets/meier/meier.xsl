<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	generic style sheet (just prints a list of fluents)

	To make your own stylesheet change the print_state template to output the state the given position and (if neccessary) change the stateWidth parameter in the main template
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	
	<xsl:template name="print_state">
		
		
		<style type="text/css" media="all">
			div.dice
			{
				position: relative;
				width:    300px;
				height:   100px;
			}
			div.claimedValues
			{
				position: relative;
				height:   80px;
			}
		</style>
		
		<div class="dice">
			
			<xsl:variable name="rolling" >
				<xsl:value-of select="fact[prop-f='ROLLING_FOR']"/>
			</xsl:variable>
			
			<xsl:if test="$rolling = ''">
			
				<xsl:for-each select="fact[prop-f='HAS_DICE']">
				
					<xsl:variable name="player">
						<xsl:choose>
						  <xsl:when test="arg[1]='P1'">black</xsl:when>
						  <xsl:otherwise>red</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
				
					<xsl:variable name="value1" select="arg[2]"/>
					<xsl:variable name="value2" select="arg[3]"/>
			
					<span class="heading"><xsl:value-of select="arg[1]"/></span> has dice:
			
					<img>
						<xsl:attribute name="width">56</xsl:attribute>
						<xsl:attribute name="height">56</xsl:attribute>
						<xsl:attribute name="src">
							<xsl:value-of select="$stylesheetURL"/>
							<xsl:text>/generic/dice_images/die_</xsl:text>
							<xsl:value-of select="$player"/>
							<xsl:text>_</xsl:text>
							<xsl:value-of select="$value1"/>
							<xsl:text>.png</xsl:text>
						</xsl:attribute>
					</img>
					|
					<img>
						<xsl:attribute name="width">56</xsl:attribute>
						<xsl:attribute name="height">56</xsl:attribute>
						<xsl:attribute name="src">
							<xsl:value-of select="$stylesheetURL"/>
							<xsl:text>/generic/dice_images/die_</xsl:text>
							<xsl:value-of select="$player"/>
							<xsl:text>_</xsl:text>
							<xsl:value-of select="$value2"/>
							<xsl:text>.png</xsl:text>
						</xsl:attribute>
					</img>
			
				</xsl:for-each>
			
			</xsl:if>
			
		</div>
		
		
		<div class="claimedValues">
			
			<xsl:for-each select="fact[prop-f='PREVIOUS_CLAIMED_VALUES']">
			
				<xsl:if test="arg[1]!=0">
				
					<xsl:variable name="value1" select="arg[1]"/>
					<xsl:variable name="value2" select="arg[2]"/>
					
					<xsl:variable name="claimingPlayer">
						
						<xsl:variable name="claiming" >
							<xsl:value-of select="../fact[prop-f='CLAIMING']/arg[1]"/>
						</xsl:variable>
						<xsl:variable name="guessing" >
							<xsl:value-of select="../fact[prop-f='GUESSING']/arg[1]"/>
						</xsl:variable>
						<xsl:variable name="rolling" >
							<xsl:value-of select="../fact[prop-f='ROLLING_FOR']/arg[1]"/>
						</xsl:variable>
						<xsl:variable name="gameOver" >
							<xsl:value-of select="../fact[prop-f='GAME_OVER']/arg[1]"/>
						</xsl:variable>
						
						<xsl:choose>
							
							<xsl:when test="$claiming!=''">
								<xsl:choose>
									<xsl:when test="$claiming='P1'">P2</xsl:when>
									<xsl:otherwise>P1</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							
							<xsl:when test="$guessing!=''">
								<xsl:choose>
									<xsl:when test="$guessing='P1'">P2</xsl:when>
									<xsl:otherwise>P1</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							
							<xsl:when test="$rolling!=''">
								<xsl:choose>
									<xsl:when test="$rolling='P1'">P2</xsl:when>
									<xsl:otherwise>P1</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							
							<xsl:when test="$gameOver!=''">
								<xsl:choose>
									<xsl:when test="$gameOver='P1'">P2</xsl:when>
									<xsl:otherwise>P1</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							
						</xsl:choose>
					</xsl:variable>
					
					<xsl:variable name="color">
						<xsl:choose>
						  <xsl:when test="$claimingPlayer='P1'">black</xsl:when>
						  <xsl:otherwise>red</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
				
					<xsl:value-of select="$claimingPlayer"/> claims:
				
					<img>
						<xsl:attribute name="width">28</xsl:attribute>
						<xsl:attribute name="height">28</xsl:attribute>
						<xsl:attribute name="src">
							<xsl:value-of select="$stylesheetURL"/>
							<xsl:text>/generic/dice_images/die_</xsl:text>
							<xsl:value-of select="$color"/>
							<xsl:text>_</xsl:text>
							<xsl:value-of select="$value1"/>
							<xsl:text>.png</xsl:text>
						</xsl:attribute>
					</img>
					|
					<img>
						<xsl:attribute name="width">28</xsl:attribute>
						<xsl:attribute name="height">28</xsl:attribute>
						<xsl:attribute name="src">
							<xsl:value-of select="$stylesheetURL"/>
							<xsl:text>/generic/dice_images/die_</xsl:text>
							<xsl:value-of select="$color"/>
							<xsl:text>_</xsl:text>
							<xsl:value-of select="$value2"/>
							<xsl:text>.png</xsl:text>
						</xsl:attribute>
					</img>
				
				</xsl:if>
		
			</xsl:for-each>
			
		</div>
		
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'PREVIOUS_CLAIMED_VALUES'"/>
			<xsl:with-param name="excludeFluent2" select="'HAS_DICE'"/>
		</xsl:call-template>

	</xsl:template>
	

</xsl:stylesheet>











