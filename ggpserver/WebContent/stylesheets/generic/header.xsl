<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing the generic game-master header.
	- For use within <body>.
	- needs css/main.css and sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="header">
		<div class="header">
			<xsl:variable name="currentStep" select="count(/match/history/step)+1"/>
			<span class="heading">Match:</span><span class="content"><xsl:value-of select="/match/match-id"/></span>
			<br/>
			<xsl:call-template name="make_tab">
				<xsl:with-param name="which">initial</xsl:with-param>
				<xsl:with-param name="currentStep" select="$currentStep"/>
			</xsl:call-template>
			<xsl:call-template name="make_tab">
				<xsl:with-param name="which">previous</xsl:with-param>
				<xsl:with-param name="currentStep" select="$currentStep"/>
			</xsl:call-template>
			<xsl:call-template name="make_tab">
				<xsl:with-param name="which">next</xsl:with-param>
				<xsl:with-param name="currentStep" select="$currentStep"/>
			</xsl:call-template>
			<xsl:call-template name="make_tab">
				<xsl:with-param name="which">final</xsl:with-param>
				<xsl:with-param name="currentStep" select="$currentStep"/>
			</xsl:call-template>
			<div class="underline" style="clear:left;"/>
		</div>
	</xsl:template>

	<xsl:template name="make_tab">
		<xsl:param name="which"/>
		<xsl:param name="currentStep"/>

		<div class="bartab">
			<a>
				<xsl:if test="(($which='initial' or $which='previous') and $currentStep != 1) or (($which='final' or $which='next') and not (/match/scores/reward))">

					<xsl:variable name="linkStep">
						<xsl:choose>
							<xsl:when test="$which='initial'">1</xsl:when>
							<xsl:when test="$which='previous'"><xsl:value-of select="$currentStep - 1"/></xsl:when>
							<xsl:when test="$which='next'"><xsl:value-of select="$currentStep + 1"/></xsl:when>
							<xsl:when test="$which='final'">final</xsl:when>
						</xsl:choose>
					</xsl:variable>

					<xsl:attribute name="href">
						<xsl:call-template name="makeStepLinkURL">
							<xsl:with-param name="step" select="$linkStep"/>
						</xsl:call-template>
					</xsl:attribute>
				</xsl:if>

				<xsl:attribute name="title">
					<xsl:value-of select="$which"/> state
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