DELETE FROM public.metadata;
DELETE FROM public.dataset_identifier;
DELETE FROM public.dataset_contact;
DELETE FROM public.dataset;
DELETE FROM public.installation;
DELETE FROM public.organization;
DELETE FROM public.node;


INSERT INTO public.node (key, gbif_region, continent, title, country, created_by, modified_by,
                         created, modified, deleted, fulltext_search, type, participation_status)
VALUES ('a49e75d9-7b07-4d01-9be8-6ab2133f42f9', 'EUROPE', 'EUROPE', 'The UK National Node', 'GB',
        'WS TEST', 'WS TEST', '2020-02-22 09:54:09.835039', '2020-02-22 09:54:09.835039', null,
        '''countri'':5 ''europ'':7,8 ''gb'':9 ''nation'':3 ''node'':4 ''uk'':2 ''vote'':6',
        'COUNTRY', 'VOTING');

INSERT INTO public.organization (key, endorsing_node_key, endorsement_approved, password, title,
                                 abbreviation, description, language, logo_url, city, province,
                                 country, postal_code, latitude, longitude, created_by, modified_by,
                                 created, modified, deleted, fulltext_search, email, phone,
                                 homepage, address, challenge_code_key)
VALUES ('ff593857-44c2-4011-be20-8403e8d0bd9a', 'a49e75d9-7b07-4d01-9be8-6ab2133f42f9', false,
        'password', 'The BGBM', 'BGBM', 'The Berlin Botanical...', 'de', 'http://www.example.org',
        'BERLIN', 'BERLIN', 'GB', '1408', null, null, 'WS TEST', 'WS TEST',
        '2020-02-22 09:54:09.988088', '2020-02-22 09:54:09.988088', null,
        '''1408'':16 ''2920202'':9 ''a@b.com'':8 ''berlin'':5,12,13,14 ''bgbm'':2,3 ''botan'':6 ''de'':7 ''gb'':15 ''www.example.org'':10,11',
        '{a@b.com}', '{2920202}', '{http://www.example.org}', '{Berliner}', null);

INSERT INTO public.installation (key, organization_key, type, title, description, created_by,
                                 modified_by, created, modified, deleted, fulltext_search, password,
                                 disabled)
VALUES ('1e9136f0-78fd-40cd-8b25-26c78a376d8d', 'ff593857-44c2-4011-be20-8403e8d0bd9a',
        'IPT_INSTALLATION', 'The BGBM BIOCASE INSTALLATION', 'The Berlin Botanical...', 'WS TEST',
        'WS TEST', '2020-02-22 09:54:10.094782', '2020-02-22 09:54:10.094782', null,
        '''berlin'':8 ''bgbm'':2 ''biocas'':3 ''botan'':9 ''instal'':4,6 ''ipt'':5', null, false);

INSERT INTO public.dataset (key, parent_dataset_key, duplicate_of_dataset_key, installation_key, publishing_organization_key, external, type, sub_type, title, alias, abbreviation, description, language, homepage, logo_url, citation, citation_identifier, rights, locked_for_auto_update, created_by, modified_by, created, modified, deleted, fulltext_search, doi, license, maintenance_update_frequency, version)
VALUES ('38f06820-08c5-42b2-94f6-47cc3e83a54a', null, null, '1e9136f0-78fd-40cd-8b25-26c78a376d8d', 'ff593857-44c2-4011-be20-8403e8d0bd9a', false, 'CHECKLIST', null, 'DatasetUpdater test dataset', 'BGBM', 'BGBM', 'Test dataset', 'da', 'http://www.example.org', 'http://www.example.org', 'This is a citation text', 'ABC', 'The rights', false, 'CLI TEST', 'CLI TEST', '2020-02-22 09:54:10.223198', '2020-02-21 23:00:00.000000', null, '''255'':5 ''aladaglari'':44 ''berlin'':50 ''bgbm'':47,48 ''bolkar'':42 ''botan'':51 ''charact'':6,28,30 ''checklist'':46 ''citat'':56 ''daglari'':43 ''der'':39,41 ''exact'':33 ''german'':22 ''hochgebirgsregion'':40 ''languag'':23 ''long'':18 ''need'':2 ''pontaurus'':1 ''text'':57 ''titl'':10,19,34 ''turkei'':45 ''untersuchungen'':37 ''vegetationskundlich'':36 ''word'':24,26 ''www.example.org'':52', '10.21373/gbif.2014.xsd123', 'CC_BY_NC_4_0', null, null);


INSERT INTO public.metadata (key, dataset_key, type, content, created_by, modified_by, created, modified) VALUES (-1, '38f06820-08c5-42b2-94f6-47cc3e83a54a', 'EML', '<eml:eml xmlns:eml="eml://ecoinformatics.org/eml-2.1.1"
  xmlns:dc="http://purl.org/dc/terms/"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="eml://ecoinformatics.org/eml-2.1.1 http://rs.gbif.org/schema/eml-gbif-profile/1.1/eml.xsd"
  packageId="38f06820-08c5-42b2-94f6-47cc3e83a54a/v2.1" system="http://gbif.org" scope="system"
  xml:lang="eng">

  <dataset>
    <alternateIdentifier>doi:10.21373/gbif.2014.xsd123</alternateIdentifier>
    <alternateIdentifier>38f06820-08c5-42b2-94f6-47cc3e83a54a</alternateIdentifier>
    <alternateIdentifier>http://ipt.ala.org.au/resource?r=global</alternateIdentifier>
    <title xml:lang="eng">DatasetUpdater test dataset</title>
    <creator>
      <individualName>
        <givenName>Graham J</givenName>
        <surName>Edgar</surName>
      </individualName>
      <organizationName>Reef Life Survey Foundation</organizationName>
      <positionName>President</positionName>
      <address>
        <deliveryPoint>c/o IMAS, Private Bag 49</deliveryPoint>
        <city>Hobart</city>
        <administrativeArea>Tasmania</administrativeArea>
        <postalCode>7001</postalCode>
        <country>AU</country>
      </address>
      <userId directory="http://orcid.org/">0000-0003-0833-9001</userId>
    </creator>
    <creator>
      <individualName>
        <givenName>Rick D</givenName>
        <surName>Stuart-Smith</surName>
      </individualName>
      <organizationName>Reef Life Survey Foundation</organizationName>
      <positionName>Executive Officer</positionName>
      <address>
        <deliveryPoint>c/o IMAS, Private Bag 49</deliveryPoint>
        <city>Hobart</city>
        <administrativeArea>Tasmania</administrativeArea>
        <postalCode>7001</postalCode>
        <country>AU</country>
      </address>
      <electronicMailAddress>rick.stuartsmith@utas.edu.au</electronicMailAddress>
      <userId directory="http://orcid.org/">0000-0002-8874-0083</userId>
    </creator>
    <metadataProvider>
      <individualName>
        <givenName>Graham J</givenName>
        <surName>Edgar</surName>
      </individualName>
      <organizationName>Reef Life Survey Foundation</organizationName>
      <positionName>President</positionName>
      <address>
        <deliveryPoint>c/o IMAS, Private Bag 49</deliveryPoint>
        <city>Hobart</city>
        <administrativeArea>Tasmania</administrativeArea>
        <postalCode>7001</postalCode>
        <country>AU</country>
      </address>
      <userId directory="http://orcid.org/">0000-0003-0833-9001</userId>
    </metadataProvider>
    <associatedParty>
      <individualName>
        <givenName>Graham J</givenName>
        <surName>Edgar</surName>
      </individualName>
      <organizationName>Institute for Marine and Antarctic Studies (IMAS), University of Tasmania (UTAS)</organizationName>
      <positionName>Professor</positionName>
      <address>
        <deliveryPoint>Institute for Marine and Antarctic Studies, Private Bag 49</deliveryPoint>
        <city>Hobart</city>
        <administrativeArea>Tasmania</administrativeArea>
        <postalCode>7001</postalCode>
        <country>AU</country>
      </address>
      <electronicMailAddress>g.edgar@utas.edu.au</electronicMailAddress>
      <userId directory="http://orcid.org/">0000-0003-0833-9001</userId>
      <role>principalInvestigator</role>
    </associatedParty>
    <associatedParty>
      <individualName>
        <givenName>Kyle</givenName>
        <surName>Braak</surName>
      </individualName>
      <organizationName>GBIF</organizationName>
      <userId directory="http://orcid.org/">0000-0002-3696-3496</userId>
      <role>processor</role>
    </associatedParty>
    <pubDate>
      2016-12-23
    </pubDate>
    <language>eng</language>
    <abstract>
      <para>This dataset contains records of bony fishes and elasmobranchs collected by Reef Life Survey (RLS) divers along 50 m transects on shallow rocky and coral reefs, worldwide. Abundance information is available for all records found within quantitative survey limits (50 x 5 m swathes during a single swim either side of the transect line, each distinguished as a Block), and out-of-survey records are identified as presence-only (Method 0).</para>
    </abstract>
    <keywordSet>
      <keyword>Occurrence</keyword>
      <keywordThesaurus>GBIF Dataset Type Vocabulary: http://rs.gbif.org/vocabulary/gbif/dataset_type.xml</keywordThesaurus>
    </keywordSet>
    <keywordSet>
      <keyword>FISH (theme)</keyword>
      <keywordThesaurus>Global Change Master Directory (GCMD) Earth Science Keywords Version 8.0</keywordThesaurus>
    </keywordSet>
    <additionalInfo>
      <para>Please note that occurrenceIDs in this dataset are currently not stable.</para>
    </additionalInfo>
    <intellectualRights>
      <para>This work is licensed under a <ulink url="http://creativecommons.org/licenses/by/4.0/legalcode"><citetitle>Creative Commons Attribution (CC-BY) 4.0 License</citetitle></ulink>.</para>
    </intellectualRights>
    <distribution scope="document">
      <online>
        <url function="information">http://reeflifesurvey.com/</url>
      </online>
    </distribution>
    <coverage>
      <geographicCoverage>
        <geographicDescription>The Reef Life Survey reef fish dataset covers more than 2,500 sites in coral and rocky reefs distributed worldwide.</geographicDescription>
        <boundingCoordinates>
          <westBoundingCoordinate>-180</westBoundingCoordinate>
          <eastBoundingCoordinate>180</eastBoundingCoordinate>
          <northBoundingCoordinate>90</northBoundingCoordinate>
          <southBoundingCoordinate>-90</southBoundingCoordinate>
        </boundingCoordinates>
      </geographicCoverage>
      <taxonomicCoverage>
        <generalTaxonomicCoverage>The Reef Life Survey global reef fish dataset contains records of more than 2,600 fish taxa, and is expanding as more surveys are undertaken in new locations</generalTaxonomicCoverage>
        <taxonomicClassification>
          <taxonRankName>phylum</taxonRankName>
          <taxonRankValue>Chordata</taxonRankValue>
        </taxonomicClassification>
        <taxonomicClassification>
          <taxonRankName>kingdom</taxonRankName>
          <taxonRankValue>Animalia</taxonRankValue>
        </taxonomicClassification>
      </taxonomicCoverage>
    </coverage>
    <maintenance>
      <description>
        <para>Data are updated in intervals that are uneven in duration. Updates include correction of errors in previously added data, and addition of new data.</para>
      </description>
      <maintenanceUpdateFrequency>continually</maintenanceUpdateFrequency>
    </maintenance>

    <contact>
      <individualName>
        <givenName>Graham J</givenName>
        <surName>Edgar</surName>
      </individualName>
      <organizationName>Reef Life Survey Foundation</organizationName>
      <positionName>President</positionName>
      <address>
        <deliveryPoint>c/o IMAS, Private Bag 49</deliveryPoint>
        <city>Hobart</city>
        <administrativeArea>Tasmania</administrativeArea>
        <postalCode>7001</postalCode>
        <country>AU</country>
      </address>
      <userId directory="http://orcid.org/">0000-0003-0833-9001</userId>
    </contact>
    <contact>
      <individualName>
        <givenName>Rick D</givenName>
        <surName>Stuart-Smith</surName>
      </individualName>
      <organizationName>Reef Life Survey Foundation</organizationName>
      <positionName>Executive Officer</positionName>
      <address>
        <deliveryPoint>c/o IMAS, Private Bag 49</deliveryPoint>
        <city>Hobart</city>
        <postalCode>7001</postalCode>
        <country>AU</country>
      </address>
      <electronicMailAddress>rick.stuartsmith@utas.edu.au</electronicMailAddress>
      <userId directory="http://orcid.org/">0000-0002-8874-0083</userId>
    </contact>
    <methods>
      <methodStep>
        <description>
          <para>This dataset is compiled from data collected in a combination of collaborative surveys with scientists, targeted RLS field campaigns and ad-hoc local surveys by trained RLS divers at their regular dive sites or when on holidays. Field campaigns involve small groups of divers (usually 4 to 8) undertaking survey dives over a period of four days to two weeks (or occasionally longer) under the direction and supervision of a scientist or experienced survey diver. At the conclusion of each field campaign, one of the RLS organizers or scientists leading the trip collates data from participants and undertakes manual checks of the data. These checks included close scrutiny of species lists, abundances and site details. Evidence in the form of images is usually requested for records of species not seen by the experienced surveyor on the trip, with such evidence essential for divers with less experience in that particular region. Uncertain records or records of new species for regions for which definitive evidence is not available are reduced to the highest taxonomic resolution for which there is confidence (usually genus). For ad-hoc surveys by trained divers outside of group field campaigns, species identification assistance and data transfer occur via email, and all the data checks are made by a scientist in the office before uploading data to the database.</para>
        </description>
      </methodStep>
      <sampling>
        <studyExtent>
          <description>
            <para>More than 2,500 reef locations across the globe. New data are regularly collected and the dataset frequently updated.</para>
          </description>
        </studyExtent>
        <samplingDescription>
          <para>The RLS global reef fish dataset includes data from more than 2,500 sites, collected using standard RLS survey methods, described in detail in an online methods manual (Reef Life Survey methods manual. http://reeflifesurvey.com/reef-life-survey/about-rls/methods/). Surveys involve underwater visual census (UVC) by SCUBA divers along a 50 m transect line, laid along a depth contour on hard substrate (coral or rocky reef). All fish species observed within 5 m of the transect line were recorded on a waterproof datasheet as divers swim slowly along the line.

            Abundance estimates are made by keeping a tally of individuals of less abundant species and, in locations with high fish densities, estimating the number of more abundant species. Abundances of schooling fishes are recorded by counting a subset within the school which is combined with an estimate of the proportion of the total school. In coral reefs with high fish species richness and densities, the order of priority for recording accurately is to first ensure all species observed along transects are included, then tallies of individuals of larger or rare species, then finally estimates of abundance for more common species. Only divers with the most extensive and appropriate experience undertake surveys in diverse coral reefs.

            Nearly all fishes observed are identified to species level, with photographs of unknown species taken with an underwater digital camera for later identification using appropriate field guides and consultation with taxonomic experts for the particular group, as necessary. When species level identification is not possible, records are classified at the highest taxonomic resolution possible given the information available and experience of the observer. As of 2015, 1.8% of records in the RLS global reef fish dataset were not at the species level (i.e., are at genus level or higher).

            Fishes within 5 m of the line are recorded separately for each side of the transect line, with each side referred to as a ‘block’. Thus, two blocks form a complete transect (also referred to as an individual survey). Multiple transects are usually surveyed at each site, usually along different depth contours. Sites are distinguished by unique site codes with latitude and longitude recorded in decimal degrees (WGS84) using a handheld GPS unit, or taken from Google Earth.</para>
        </samplingDescription>
      </sampling>
      <qualityControl>
        <description>
          <para>Data in the RLS global reef fish dataset have been collected by a combination of experienced scientists and skilled recreational divers, with all divers having either substantial prior experience in reef fish surveys or extensive training in the RLS methods. Screening of interested divers is undertaken before training so that only the most committed and capable divers with appropriate SCUBA experience are invited to participate. Although a minimum of 50 dives’ experience is used as a standard in diver selection, a survey of RLS divers in 2010 indicated that most RLS divers had completed over 300 dives. For divers without prior formal scientific training, one-on-one instruction in survey methods and assistance with species identification is provided during a training course typically lasting four to five days, but up to two weeks (depending on local marine life and skills of the diver). During these courses, trainees undertake practice surveys with an experienced scientist, who carefully compares their data following each dive, with a final approval given after data are considered to be of high consistency with the trainer. A formal comparison of data collected by divers without tertiary scientific training with data collected by experienced scientists showed that the variation between recreational and scientific divers was non-significant and negligible in comparison to other sources of variation within and between sites.

            Following each survey, each RLS diver transcribes their data from the underwater datasheets onto custom data entry forms. This is usually done the same day as survey dives are undertaken. Data entry templates contain lookups from region-specific species lists. Data checks are made upon upload of forms to the database, including for data structure (and completeness) and consistency in metadata among divers, as well as checks designed to detect species not previously recorded by RLS divers in that particular region. Any new species added for a given region prompt querying of the records, and taxonomic and distributional data are also checked before addition of new species to the database.</para>
        </description>
      </qualityControl>
    </methods>
    <project >
      <title>Reef Life Survey</title>
      <personnel>
        <individualName>
          <givenName>Graham J.</givenName>
          <surName>Edgar</surName>
        </individualName>
        <role>principalInvestigator</role>
      </personnel>
      <personnel>
        <individualName>
          <givenName>Rick D</givenName>
          <surName>Stuart-Smith</surName>
        </individualName>
        <role>principalInvestigator</role>
      </personnel>
      <abstract>
        <para>Reef Life Survey (RLS) aims to improve biodiversity conservation and the sustainable management of marine resources by coordinating surveys of rocky and coral reefs using scientific methods. RLS activities depend on the skills of experienced and motivated recreational SCUBA divers and partnerships with management agencies and university researchers.</para>
      </abstract>
      <funding>
        <para>RLS activities are presently funded primarily through grants from biodiversity management agencies and philanthropic organisations, and with in-kind contributions and support from SCUBA charter operators and shops. Further funding sources and sustainable funding models are being sought to allow long-term continuation of RLS data collection at larger scales.</para>
      </funding>
      <studyAreaDescription>
        <descriptor name="generic"
          citableClassificationSystem="false">
          <descriptorValue>RLS surveys have been undertaken on shallow rocky and coral reefs in all ocean basins and from the Arctic to the Antarctic.</descriptorValue>
        </descriptor>
      </studyAreaDescription>
      <designDescription>
        <description>
          <para>As of December 2015, the majority of global data represent once-off surveys designed to fill spatial gaps and provide an extensive coverage of reefs around the world. Time-series data from repeat surveys of particular reef sites as part of a monitoring program has largely been confined to some key locations in Australia. A key goal of RLS is to extend to number of locations at which time-series data are collected, undertaking monitoring in many more countries.</para>
        </description>
      </designDescription>
    </project>
  </dataset>
  <additionalMetadata>
    <metadata>
      <gbif>
        <dateStamp>2014-06-25T11:18:37.647+00:00</dateStamp>
        <hierarchyLevel>dataset</hierarchyLevel>
        <citation identifier="http://doi.org/10.15468/qjgwba">Edgar G J, Stuart-Smith R D (2014): Reef Life Survey: Global reef fish dataset. v2.1. Reef Life Survey. Dataset/Samplingevent. http://doi.org/10.15468/qjgwba</citation>
        <bibliography>
          <citation identifier="doi:10.1038/sdata.2014.7">Edgar, GJ and Stuart-Smith, RD, “Systematic global assessment of reef fish communities by the Reef Life Survey program”, Scientific Data, 1 Article 140007. doi:10.1038/sdata.2014.7 ISSN 2052-4463 (2014)</citation>
          <citation identifier="doi:10.1038/nature13022">Edgar, GJ and Stuart-Smith, RD and Willis, TJ* and Kininmonth, S* and Baker, SC and Banks, S* and Barrett, NS and Becerro, MA* and Bernard, ATF* and Berkhout, J and Buxton, CD and Campbell, SJ* and Cooper, AT and Davey, M and Edgar, SC* and Forsterra, G* and Galvan, DE* and Irigoyen, AJ* and Kushner, DJ* and Moura, R* and Parnell, PE* and Shears, NT* and Soler, G and Strain, EMA* and Thomson, RJ, “Global conservation outcomes depend on marine protected areas with five key features”, Nature, 506 (7487) pp. 216-220. doi:10.1038/nature13022 ISSN 0028-0836 (2014)</citation>
          <citation identifier="doi:10.1016/j.biocon.2013.07.037">Bird, TJ and Bates, AE and Lefcheck, JS* and Hill, NA and Thomson, RJ and Edgar, GJ and Stuart-Smith, RD and Wotherspoon, S and Krkosek, M* and Stuart-Smith, JF and Pecl, GT and Barrett, N and Frusher, S, “Statistical solutions for error and bias in global citizen science datasets”, Biological Conservation, 173 pp. 144-154. doi:10.1016/j.biocon.2013.07.037 ISSN 0006-3207 (2014)</citation>
          <citation identifier="doi:10.1038/nature12529">Stuart-Smith, RD and Bates, AE and Lefcheck, JS* and Duffy, JE* and Baker, SC and Thomson, RJ and Stuart-Smith, JF and Hill, NA and Kininmonth, SJ* and Airoldi, L* and Becerro, MA* and Campbell, SJ* and Dawson, TP* and Navarrete, SA* and Soler, GA and Strain, EMA* and Willis, TJ* and Edgar, GJ, “Integrating abundance and functional traits reveals new global hotspots of fish diversity”, Nature, 501 pp. 539-542. doi:10.1038/nature12529 ISSN 0028-0836 (2013)</citation>
          <citation identifier="ISSN 1051-0761">Edgar, GJ and Barrett, NS and Stuart-Smith, RD, ‘Exploited reefs protected from fishing transform over decades into conservation features otherwise absent from seascapes’, Ecological Applications, 19 (8) pp. 1967-1974. ISSN 1051-0761 (2009)</citation>
          <citation identifier="ISSN 0171-8630">Edgar, GJ and Stuart-Smith, RD, ‘Ecological effects of marine protected areas on rocky reef communities - a continental-scale analysis’, Marine Ecology Progress Series, 388 pp. 51-62. ISSN 0171-8630 (2009)</citation>
          <citation identifier="doi:10.1038/nature16144">Stuart-Smith, RD and Edgar, GJ and Barrett, NS and Kininmonth, S and Bates AE, &apos;Thermal biases and vulnerability to warming in the world’s marine fauna&apos;, Nature, 528, pp. 88-92. doi:10.1038/nature16144 (2015)</citation>
        </bibliography>
        <physical>
          <objectName>Figshare archive of original data published on 2014-05-22</objectName>
          <characterEncoding>Mac OS Roman</characterEncoding>
          <dataFormat>
            <externallyDefinedFormat>
              <formatName>CSV</formatName>
            </externallyDefinedFormat>
          </dataFormat>
          <distribution>
            <online>
              <url function="download">http://dx.doi.org/10.6084/m9.figshare.934319</url>
            </online>
          </distribution>
        </physical>
        <resourceLogoUrl>http://ipt.ala.org.au/logo.do?r=global</resourceLogoUrl>
        <formationPeriod>2006 onwards</formationPeriod>
        <dc:replaces>38f06820-08c5-42b2-94f6-47cc3e83a54a/v2.1.xml</dc:replaces>
      </gbif>
    </metadata>
  </additionalMetadata>
</eml:eml>
', 'crawler.gbif.org', 'crawler.gbif.org', '2016-12-25 01:53:27.548126', '2016-12-25 01:53:27.548126');

