<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	<xsl:import href="../generic/dice.xsl"/>
	
	
	<xsl:template name="print_state">
		
		<style type="text/css" media="all">
			div.dice
			{
				width:    300px;
				padding-top:20px; padding-bottom:20px;
				padding-right:20px; padding-left:20px;
			}
		</style>
		
		<div class="dice">
			
			<xsl:variable name="rolling" >
				<xsl:value-of select="fact[prop-f='ROLLING_FOR']"/>
			</xsl:variable>
			
			<xsl:if test="$rolling = ''">
			
				<xsl:for-each select="fact[prop-f='HAS_DICE']">
				
					<xsl:variable name="color">
						<xsl:choose>
						  <xsl:when test="arg[1]='P1'">black</xsl:when>
						  <xsl:otherwise>red</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					
					<xsl:variable name="value1" select="arg[2]"/>
					<xsl:variable name="value2" select="arg[3]"/>
			
					<span class="heading"><xsl:value-of select="arg[1]"/></span> has dice:<br/>
					
					<xsl:call-template name="print_dice">
						<xsl:with-param name="color" select="$color"/>
						<xsl:with-param name="value" select="$value1"/>
					</xsl:call-template>
					|
					<xsl:call-template name="print_dice">
						<xsl:with-param name="color" select="$color"/>
						<xsl:with-param name="value" select="$value2"/>
					</xsl:call-template>
					
				</xsl:for-each>
			
			</xsl:if>
			
			<br/>
			
			<xsl:for-each select="fact[prop-f='PREVIOUS_CLAIMED_VALUES']">
			
				<xsl:if test="arg[1]!=0">
				
					<xsl:variable name="value1" select="arg[1]"/>
					<xsl:variable name="value2" select="arg[2]"/>
					
					<xsl:variable name="claimingPlayer">
						<xsl:choose>
							<xsl:when test="../fact[prop-f='CLAIMING' or prop-f='GUESSING' or prop-f='ROLLING_FOR' or prop-f='GAME_OVER']/arg[1] = 'P1'">P2</xsl:when>
							<xsl:otherwise>P1</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					
					<xsl:variable name="color">
						<xsl:choose>
						  <xsl:when test="$claimingPlayer='P1'">black</xsl:when>
						  <xsl:otherwise>red</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
				
					<span class="heading"><xsl:value-of select="$claimingPlayer"/></span> claims:<br/>
				
					<xsl:call-template name="print_dice">
						<xsl:with-param name="color" select="$color"/>
						<xsl:with-param name="value" select="$value1"/>
						<xsl:with-param name="width" select="'28'"/>
					</xsl:call-template>
					|
					<xsl:call-template name="print_dice">
						<xsl:with-param name="color" select="$color"/>
						<xsl:with-param name="value" select="$value2"/>
						<xsl:with-param name="width" select="'28'"/>
					</xsl:call-template>
				</xsl:if>
			
			</xsl:for-each>
			
		</div>
		
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'PREVIOUS_CLAIMED_VALUES'"/>
			<xsl:with-param name="excludeFluent2" select="'HAS_DICE'"/>
		</xsl:call-template>

	</xsl:template>
	
	
	<xsl:template name="legalMove">
		<xsl:param name="url"/> <!-- the URL to propose this move (no confirm) -->
		
		<xsl:variable name="color">
			black
		</xsl:variable>
		<xsl:variable name="prop-f" select="./move-term/prop-f" />
		<xsl:variable name="value1" select="./move-term/arg[1]" />
		<xsl:variable name="value2" select="./move-term/arg[2]" />
		
		<style>
			a img {
				border-style: none;
			}
		</style>
		
		<tr>
			<td>
				<span class="content">
				<a>
					<xsl:attribute name="href">javascript:location.replace("<xsl:value-of select="$url" />");</xsl:attribute>
					<xsl:choose>
						<xsl:when test="$prop-f = 'CLAIM'">
							claim 
							<xsl:call-template name="print_dice">
								<xsl:with-param name="color" select="$color"/>
								<xsl:with-param name="value" select="$value1"/>
								<xsl:with-param name="width" select="'28'"/>
							</xsl:call-template>
							<xsl:call-template name="print_dice">
								<xsl:with-param name="color" select="$color"/>
								<xsl:with-param name="value" select="$value2"/>
								<xsl:with-param name="width" select="'28'"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="./move-term" />
						</xsl:otherwise>
					</xsl:choose>
				</a>
				</span>
			</td>
		</tr>
	</xsl:template>
	

</xsl:stylesheet>











