<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	templates for printing images of dice
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="sitespecific.xsl"/>

	<xsl:template name="print_dice">
		<xsl:param name="value"/>					<!-- 1, 2, ..., 6 -->
		<xsl:param name="color" select="'black'"/>	<!-- 'black' or 'red' -->
		<xsl:param name="width" select="56"/>	<!-- width and height of the image -->
		
		<img>
			<xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute>
			<xsl:attribute name="height"><xsl:value-of select="$width"/></xsl:attribute>
			<xsl:attribute name="src">
				<xsl:call-template name="make_dice_url">
					<xsl:with-param name="value" select="$value"/>
					<xsl:with-param name="color" select="$color"/>
				</xsl:call-template>
			</xsl:attribute>
		</img>
	</xsl:template>

	<!-- value = 1, 2, ..., 6 or 'blank' -->
	<!-- color = 'black' or 'red' -->
	<xsl:template name="make_dice_url">
		<xsl:param name="value"/>
		<xsl:param name="color" select="'black'"/>
		<xsl:value-of select="$stylesheetURL"/>
		<xsl:text>/generic/dice_images/die_</xsl:text>
		<xsl:value-of select="$color"/>
		<xsl:text>_</xsl:text>
		<xsl:value-of select="$value"/>
		<xsl:text>.png</xsl:text>
	</xsl:template>

</xsl:stylesheet>
