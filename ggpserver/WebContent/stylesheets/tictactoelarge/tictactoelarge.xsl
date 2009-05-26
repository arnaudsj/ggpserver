<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="Width" select="'5'"/>
			<xsl:with-param name="Height" select="'5'"/>
			<xsl:with-param name="checkered">no</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>