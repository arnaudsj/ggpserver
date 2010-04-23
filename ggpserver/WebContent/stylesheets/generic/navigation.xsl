<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing the generic game-master header.
	- For use within <body>.
	- needs css/main.css and sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="navigation">
		<!-- should be called from /public/view_state.jsp or /members/play.jsp, thus the prefix ../ is okay -->
		<style>
			div.navigation {
				display: block;
				padding-top: 2px;
				padding-bottom: 3px;
			}
			a.navLink {
				padding-left: 10px; padding-right: 10px;
			}
		</style>
		
		<div class="navigation">
			<a class="navLink">
				<xsl:attribute name="href">
					<xsl:text disable-output-escaping="yes">../index.jsp</xsl:text>
				</xsl:attribute>
				Home Page
			</a>
			
			<a class="navLink">
				<xsl:attribute name="href">
					<xsl:text disable-output-escaping="yes">../members/profile.jsp</xsl:text>
				</xsl:attribute>
				User Profile
			</a>

			<a class="navLink">
				<xsl:attribute name="href">
					<xsl:text disable-output-escaping="yes">../public/view_match.jsp?matchID=</xsl:text>
					<xsl:value-of select="/match/match-id"/>
				</xsl:attribute>
				View Match
			</a>
		</div>
		
		<div class="underline" style="clear:left;"/>
		
	</xsl:template>
	
</xsl:stylesheet>