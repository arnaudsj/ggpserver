<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Kalaha (with 10+2 pits)
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_kalaha_board"/>
		
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'PIT'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="print_kalaha_board">
	
		<style type="text/css" media="all">
			div.pit{
				width: 64px; height: 64px; padding: 0px; border: 0px;
				vertical-align: middle;
				float:left;
				}
			div.pit_full{
				width: 64px; height: 64px; padding: 0px; border: 0px;
				background: transparent url(<xsl:value-of select="$stylesheetURL"/>/kalaha/kalaha_hole_x.gif);
				vertical-align: middle;
				text-align: center;
				font: 38px arial;
				font-style: bold;
				float:left;
				}
			div.pit_with_caption{
				height: 64px;
				line-height: 64px;
				vertical-align: middle;
				text-align: center;
				}
			#P1{ position: absolute;
				top: 70px; left: 130px;
				}
			#P2{ position: absolute;
				top: 140px; left: 130px;
				}
			#P3{ position: absolute;
				top: 210px; left: 130px;
				}
			#P4{ position: absolute;
				top: 280px; left: 130px;
				}
			#P5{ position: absolute;
				top: 350px; left: 130px;
				}
			#P6{ position: absolute;
				top: 350px; left: 20px;
				}
			#P7{ position: absolute;
				top: 280px; left: 20px;
				}
			#P8{ position: absolute;
				top: 210px; left: 20px;
				}
			#P9{ position: absolute;
				top: 140px; left: 20px;
				}
			#P10{ position: absolute;
				top: 70px; left: 20px;
				}
			#PWINNORTH{ position: absolute;
				top: 20px; left: 75px;
				}
			#PWINSOUTH{ position: absolute;
				top: 400px; left: 75px;
				}
			.kalaha_board{
				position: relative;
				width:    220px;
				height:   490px;
				padding: 0px;
				border: 2px solid;
			}
		</style>

		<div class="kalaha_board">

			<xsl:for-each select="fact[prop-f='PIT']">
				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				<xsl:variable name="seeds" select="substring-after(./arg[2], 'S')"/>
				<xsl:variable name="classname">
					<xsl:choose>
						<xsl:when test="$seeds &lt; 10">pit</xsl:when>
						<xsl:otherwise>pit_full</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<div class="pit_with_caption">
					<xsl:attribute name="id"><xsl:value-of select="arg[1]"/></xsl:attribute>
					<div>
						<xsl:attribute name="class"><xsl:value-of select="$classname"/></xsl:attribute>
						<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
						<xsl:choose>
							<xsl:when test="$seeds &lt; 10">
								<img>
									<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>/kalaha/kalaha_hole_<xsl:value-of select="arg[2]"/>.gif</xsl:attribute>
									<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
									<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
								</img>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$seeds"/>
							</xsl:otherwise>
						</xsl:choose>
					</div>
					<xsl:value-of select="arg[1]"/>
				</div>
			</xsl:for-each>

		</div>
	</xsl:template>

</xsl:stylesheet>
