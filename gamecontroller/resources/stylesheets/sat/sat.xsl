<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	satisfiablity games
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_clauses"/>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'CONTAINS'"/>
			<xsl:with-param name="excludeFluent2" select="'SAT'"/>
			<xsl:with-param name="excludeFluent3" select="'CONTROL'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:key name="by-clause-id" match="fact[prop-f='CONTAINS']" use="arg[1]"/>

	<xsl:template name="print_clauses">
		<style type="text/css" media="all">
			span.variable_done {
				color: lightgray;
			}
			span.current_variable {
				color: red;
			}
		</style>
		<p style="font-weight: bold; font-size:+1;">
			<xsl:variable name="nextVar" select="fact[prop-f='CONTROL']/arg[2]"/>
			<xsl:if test="$nextVar != 'END'">
				next variable: <xsl:value-of select="$nextVar"/>
			</xsl:if>
			
			<xsl:for-each select="fact[prop-f='CONTAINS']">
				<xsl:sort data-type="number" select="number(translate(arg[2], 'NOTEG', ''))"/>
				<xsl:comment>
					clause: <xsl:value-of select="arg[1]"/> var: <xsl:value-of select="arg[2]"/>
				</xsl:comment>
				
				<xsl:variable name="thisNode" select="generate-id(.)"/>
				<xsl:variable name="nodesWithSameClauseID" select="key('by-clause-id', arg[1])"/>
				<xsl:variable name="firstNodeWithSameClauseID" select="generate-id($nodesWithSameClauseID[1])"/>
			
				<xsl:if test="$thisNode = $firstNodeWithSameClauseID">
					<xsl:variable name="clauseID" select="arg[1]"/>
					<xsl:if test="not(../fact[prop-f='SAT' and arg[1]=$clauseID])">
					<div>
					<xsl:value-of select="$clauseID"/>
					[
					<xsl:for-each select="$nodesWithSameClauseID">
						<xsl:sort data-type="number" select="number(translate(arg[2], 'NOTEG', ''))"/>
						<xsl:variable name="var">
							<xsl:choose>
								<xsl:when test="arg[2]/prop-f='NOT' or arg[2]/prop-f='NEG'">
									<xsl:value-of select="arg[2]/arg[1]"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="arg[2]"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<span>
						<xsl:if test="$nextVar = 'END' or number($var) &lt; number($nextVar)">
							<xsl:attribute name="class">variable_done</xsl:attribute>
						</xsl:if>
						<xsl:if test="number($nextVar) = number($var)">
							<xsl:attribute name="class">current_variable</xsl:attribute>
						</xsl:if>
						<xsl:choose>
							<xsl:when test="arg[2]/prop-f='NOT' or arg[2]/prop-f='NEG'">
								-<xsl:value-of select="$var"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$var"/>
							</xsl:otherwise>
						</xsl:choose>
						</span><xsl:text> </xsl:text>
					</xsl:for-each>
					]
					</div>
					</xsl:if>
				</xsl:if>
			</xsl:for-each>
		</p>
	</xsl:template>

</xsl:stylesheet>

