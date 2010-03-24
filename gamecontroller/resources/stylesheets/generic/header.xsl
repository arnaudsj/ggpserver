<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing the generic game-master header.
	- For use within <body>.
	- needs css/main.css and sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="header">
		
		<xsl:variable name="playing">
			<xsl:choose>
				<xsl:when test="count(/match/legalmoves) = 1">1</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<div class="header" id="header">
			<xsl:variable name="currentStep" select="count(/match/history/step)+1"/>
			<xsl:variable name="role" select="/match/sight-of"/>
			
			<span class="heading">Match:</span><span class="content"><xsl:value-of select="/match/match-id"/></span>
			<br/>
			<span class="heading">Step:</span><span class="content"><xsl:value-of select="$currentStep"/></span>
			<br/>
			
			<xsl:choose>
				<xsl:when test="$role='RANDOM'">
					<span class="heading">Complete Information</span>
				</xsl:when>
				<xsl:otherwise>
					<span class="heading">Seen by:</span><span class="content"><xsl:value-of select="$role"/></span>
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="$playing = 0">
					
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">initial</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">previous</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">next</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					<xsl:call-template name="make_tab">
						<xsl:with-param name="which">final</xsl:with-param>
						<xsl:with-param name="currentStep" select="$currentStep"/>
						<xsl:with-param name="role" select="$role"/>
					</xsl:call-template>
					
				</xsl:when>
			</xsl:choose>
			
			<div class="underline" style="clear:left;"/>
			
		</div>
		
		<xsl:choose>
			<xsl:when test="$playing = 0">
				<script language="JavaScript" type="text/javascript">
					// &lt;!--
					<![CDATA[
							// replace links by javascript
							var link_ids = new Array("navigation_initial", "navigation_previous", "navigation_next", "navigation_final");
							for(i=0; i<link_ids.length; i++) {
								if(document.links[link_ids[i]] != null) {
									document.links[link_ids[i]].href = 'javascript:location.replace("' + document.links[link_ids[i]].href + '")';
								}
							}
					]]>
					// --&gt;
				</script>
			</xsl:when>
		</xsl:choose>
		
	</xsl:template>



	<xsl:template name="make_tab">
		<xsl:param name="which"/>
		<xsl:param name="currentStep"/>
		<xsl:param name="role"/>

		<div class="bartab">
			<a>
				
				<xsl:attribute name="id">navigation_<xsl:value-of select="$which"/></xsl:attribute>
				
				<xsl:variable name="linkStep">
					<xsl:choose>
						<xsl:when test="$which='initial'">1</xsl:when>
						<xsl:when test="$which='previous'"><xsl:value-of select="$currentStep - 1"/></xsl:when>
						<xsl:when test="$which='next'"><xsl:value-of select="$currentStep + 1"/></xsl:when>
						<xsl:when test="$which='final'">final</xsl:when>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:if test="(($which='initial' or $which='previous') and $currentStep != 1) or (($which='final' or $which='next') and not (/match/scores/reward))">
					<xsl:attribute name="href">
						<xsl:call-template name="makeStepLinkURL">
							<xsl:with-param name="step" select="$linkStep"/>
							<xsl:with-param name="role" select="$role"/>
						</xsl:call-template>
					</xsl:attribute>
				</xsl:if>

				<xsl:attribute name="title">
					<xsl:choose>
						<xsl:when test="$which='initial'">initial state</xsl:when>
						<xsl:when test="$which='final'">final state</xsl:when>
						<xsl:otherwise>state <xsl:value-of select="$linkStep"/></xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>

				<xsl:variable name="imageName">
					<xsl:choose>
						<xsl:when test="$which='initial'">gnome-go-first</xsl:when>
						<xsl:when test="$which='previous'">gnome-go-previous</xsl:when>
						<xsl:when test="$which='next'">gnome-go-next</xsl:when>
						<xsl:when test="$which='final'">gnome-go-last</xsl:when>
					</xsl:choose>
				</xsl:variable>

				<img width="30" height="30">
					<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>/generic/images/<xsl:value-of select="normalize-space($imageName)"/>.png</xsl:attribute>
				</img>
			</a>
		</div>
	</xsl:template>

</xsl:stylesheet>