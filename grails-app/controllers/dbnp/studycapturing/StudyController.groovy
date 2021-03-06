package dbnp.studycapturing

import grails.plugins.springsecurity.Secured
import org.dbnp.gdt.TemplateFieldType
import org.dbnp.gdt.RelTime
import grails.converters.JSON

/**
 * Controller class for studies
 */
class StudyController {
    def authenticationService
    
    //static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    /**
     * Shows all studies where the user has access to
     */
    def list = {

        def user = authenticationService.getLoggedInUser()
        def max = Math.min(params.max ? params.int('max') : 10, 100)
		def offset = params.offset ? params.int( 'offset' ) : 0
        def studies = Study.giveReadableStudies( user, max, offset );
        [studyInstanceList: studies, studyInstanceTotal: Study.countReadableStudies( user ), loggedInUser: user]

    }

    /**
     * Shows studies for which the logged in user is the owner
     */
    @Secured(['IS_AUTHENTICATED_REMEMBERED'])
    def myStudies = {
        def user = authenticationService.getLoggedInUser()
        def max = Math.min(params.max ? params.int('max') : 10, 100)
		def offset = params.offset ? params.int( 'offset' ) : 0
		
        def studies = Study.findAllByOwner(user, [max:max,offset: offset]);
        render( view: "list", model: [studyInstanceList: studies, studyInstanceTotal: Study.countByOwner(user), loggedInUser: user] )
    }

    /**
     * Shows a comparison of multiple studies using the show view
     * 
     */
    def list_extended = {
        def id = (params.containsKey('id')) ? params.get('id') : 0;
        def numberOfStudies = Study.count()
        def studyList;

        // do we have a study id?
        if (id == 0) {
            // no, go back to the overview
            redirect(action: 'list');
        } else if (id instanceof String) {
            // yes, one study. Show it
            redirect(action: 'show', id: id)
        } else {
            // multiple studies, compare them
            def c = Study.createCriteria()
            studyList = c {
                'in'("id", id.collect { Long.parseLong(it) })
            }
            render(view:'show',model:[studyList: studyList, studyInstanceTotal: numberOfStudies, multipleStudies:(studyList instanceof ArrayList)])
        }
    }

    /**
     * Shows one or more studies
     */
    def show = {
        def startTime = System.currentTimeMillis()



        def studyInstance = Study.get( params.long( "id" ) )
        if (!studyInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'study.label', default: 'Study'), params.id])}"
            redirect(action: "list")
        }
        else {
            // Check whether the user may see this study
            def loggedInUser = authenticationService.getLoggedInUser()
            if( !studyInstance.canRead(loggedInUser) ) {
                flash.message = "You have no access to this study"
                redirect(action: "list")
            }

            // The study instance is packed into an array, to be able to
            // use the same view for showing the study and comparing multiple
            // studies
            [studyList: [ studyInstance ], multipleStudies: false, loggedInUser: loggedInUser, facebookLikeUrl: "/study/show/${studyInstance?.id}" ]
        }
    }

	/**
     * Shows the subjects tab of one or more studies. Is called when opening the subjects-tab
	 * on the study overview screen.
     */
    def show_subjects = {
		def studyList = readStudies( params.id );

		if( !studyList )
			return

		[studyList: studyList, studyInstanceTotal: Study.count(), multipleStudies: ( studyList.size() > 1 ), loggedInUser: authenticationService.getLoggedInUser() ]
    }

	/**
     * Shows the events timeline tab of one or more studies. Is called when opening the events timeline-tab
	 * on the study overview screen.
     */
    def show_events_timeline = {
		def studyList = readStudies( params.id );

		if( !studyList )
			return

		[studyList: studyList, studyInstanceTotal: Study.count(), multipleStudies: ( studyList.size() > 1 ), loggedInUser: authenticationService.getLoggedInUser() ]
    }

	/**
     * Shows the events table tab of one or more studies. Is called when opening the events table-tab
	 * on the study overview screen.
     */
    def show_events_table = {
		def studyList = readStudies( params.id );

		if( !studyList )
			return

		[studyList: studyList, studyInstanceTotal: Study.count(), multipleStudies: ( studyList.size() > 1 ), loggedInUser: authenticationService.getLoggedInUser() ]
    }

	/**
     * Shows the assays tab of one or more studies. Is called when opening the assays tab
	 * on the study overview screen.
     */
    def show_assays = {
		def studyList = readStudies( params.id );

		if( !studyList )
			return

		[studyList: studyList, studyInstanceTotal: Study.count(), multipleStudies: ( studyList.size() > 1 ), loggedInUser: authenticationService.getLoggedInUser() ]
    }

	/**
     * Shows the samples tab of one or more studies. Is called when opening the samples-tab
	 * on the study overview screen.
     */
    def show_samples = {
		def studyList = readStudies( params.id );

		if( !studyList )
			return

		[studyList: studyList, studyInstanceTotal: Study.count(), multipleStudies: ( studyList.size() > 1 ), loggedInUser: authenticationService.getLoggedInUser() ]
    }

	/**
     * Shows the persons tab of one or more studies. Is called when opening the persons tab
	 * on the study overview screen.
     */
    def show_persons = {
		def studyList = readStudies( params.id );

		if( !studyList )
			return

		[studyList: studyList, studyInstanceTotal: Study.count(), multipleStudies: ( studyList.size() > 1 ), loggedInUser: authenticationService.getLoggedInUser() ]
    }

	/**
     * Shows the publications tab of one or more studies. Is called when opening the publications tab
	 * on the study overview screen.
     */
    def show_publications = {
		def studyList = readStudies( params.id );

		if( !studyList )
			return

		[studyList: studyList, studyInstanceTotal: Study.count(), multipleStudies: ( studyList.size() > 1 ), loggedInUser: authenticationService.getLoggedInUser() ]
    }

	/**
	 * Creates the javascript for showing the timeline of one or more studies
	 */
	def createTimelineBandsJs = {
		def studyList = readStudies( params.id );

		if( !studyList )
			return

		[studyList: studyList, studyInstanceTotal: Study.count(), multipleStudies: ( studyList.size() > 1 ) ]
	}

    /**
	 * Reads one or more studies from the database and checks whether the logged
	 * in user is allowed to access them.
	 * 
	 * Is used by several show_-methods
	 *
	 * @return List with Study objects or false if an error occurred.
	 */
	private def readStudies( id ) {
		// If nothing has been selected, redirect the user
		if( !id || !( id instanceof String)) {
            response.status = 500;
            render 'No study selected';
            return false
		}

		// Check whether one id has been selected or multiple.
		def ids = URLDecoder.decode( id ).split( "," );

		// Parse strings to a long
		def long_ids = []
		ids.each { long_ids.add( Long.parseLong( it ) ) }

		def c = Study.createCriteria()

        def studyList = c {
			maxResults( Math.min(params.max ? params.int('max') : 10, 100) )
			'in'( "id", long_ids )
		}

		// Check whether the user may see these studies
		def studiesAllowed = []
        def loggedInUser = authenticationService.getLoggedInUser()

		studyList.each { studyInstance ->
            if( studyInstance.canRead(loggedInUser) ) {
				studiesAllowed << studyInstance
            }
		}

		// If the user is not allowed to see any of the studies, return 404
		if( studiesAllowed.size() == 0 ) {
            response.status = 404;
            render 'Selected studies not found';
            return false
		}
		
		return studyList
	}

    def showByToken = {
        def studyInstance = Study.findWhere(UUID: params.id)
        if (!studyInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'study.label', default: 'Study'), params.id])}"
            redirect(action: "list")
        }
        else {
            // Check whether the user may see this study
            def loggedInUser = authenticationService.getLoggedInUser()
            if( !studyInstance.canRead(loggedInUser) ) {
                flash.message = "You have no access to this study"
                redirect(action: "list")
            }

            redirect(action: "show", id: studyInstance.id)
        }
    }

    /**
     * Gives the events for one eventgroup in JSON format
     *
     */
    def events = {
        def eventGroupId = Integer.parseInt( params.id );
        def studyId      = Integer.parseInt( params.study );
        def eventGroup;

        // eventGroupId == -1 means that the orphaned events should be given
        if( eventGroupId == -1 ) {
            def studyInstance = Study.get( studyId )
            
            if (studyInstance == null) {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'study.label', default: 'Study'), studyId])}"
                redirect(action: "list");
                return;
            }

            events = studyInstance.getOrphanEvents();
        } else {
            eventGroup = EventGroup.get(params.id)

            if (eventGroup == null) {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'eventgroup.label', default: 'Eventgroup'), params.id])}"
                redirect(action: "list");
                return;
            }
            events = eventGroup?.events + eventGroup?.samplingEvents;
        }

        // This parameter should give the startdate of the study in milliseconds
        // since 1-1-1970
        long startDate  = Long.parseLong( params.startDate )

        // Create JSON object
        def json = [ 'dateTimeFormat': 'iso8601', events: [] ];

        // Add all other events
        for( event in events ) {
            def parameters = []
            for( templateField in event.giveTemplateFields() ) {
                def value = event.getFieldValue( templateField.name );
				if( value ) {
					if( templateField.type == TemplateFieldType.RELTIME )
						value = new RelTime( value ).toString();

	                def param = templateField.name + " = " + value;

					if( templateField.unit )
						param += templateField.unit;

                    parameters << param ;
                }
            }

			def description = parameters.join( '<br />\n' );

			if( event instanceof SamplingEvent ) {
				 json.events << [
					'start':    new Date( startDate + event.startTime * 1000 ),
					'end':      new Date( startDate + event.startTime * 1000 ),
					'durationEvent': false,
					'title': event.template?.name,
					'description': "SampleTemplate = " + event.sampleTemplate + "<br />\n" + description
				]
			} else {
				 json.events << [
					'start':    new Date( startDate + event.startTime * 1000 ),
					'end':      new Date( startDate + event.endTime * 1000 ),
					'durationEvent': true,
					'title': event.template?.name,
					'description': description
				]
				
			}
        }

		// set output header to json
		response.contentType = 'application/json'

        render json as JSON
    }

    def delete = {
        def studyInstance = Study.get(params.id)
        if (studyInstance) {
            try {
                studyInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'study.label', default: 'Study'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'study.label', default: 'Study'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'study.label', default: 'Study'), params.id])}"
            redirect(action: "list")
        }
    }

    /**
     * Renders assay names and id's as JSON
     */
    def ajaxGetAssays = {
        def study = Study.read(params.id)

		// set output header to json
		response.contentType = 'application/json'

        render ((study?.assays?.collect{[name: it.name, id: it.id]} ?: []) as JSON)
    }
	
	/**
	 * Exports all data from the given studies to excel. This is done using a redirect to the 
	 * assay controller
	 * 
	 * @param	ids				ids of the studies to export
	 * @param	params.format	"list" in order to export all assays in one big excel sheet
	 * 							"sheets" in order to export every assay on its own sheet (default)
	 * @see		AssayController.exportToExcel
	 */
	def exportToExcel = {
		def ids = params.list( 'ids' ).findAll { it.isLong() }.collect { Long.valueOf( it ) };
		def tokens = params.list( 'tokens' );

		if( !ids && !tokens ) {
			flash.errorMessage = "No study ids given";
			redirect( controller: "assay", action: "errorPage" );
			return;
		}
		
		// Find all assay ids for these studies
		def assayIds = [];
		ids.each { id ->
			def study = Study.get( id );
			if( study ) {
				assayIds += study.assays.collect { assay -> assay.id }
			}
		}
		
		// Also accept tokens for defining studies
 		tokens.each { token ->
			def study = Study.findWhere(UUID: token)
			if( study )
				assayIds += study.assays.collect { assay -> assay.id }
		}
		 
		if( !assayIds ) {
			flash.errorMessage = "No assays found for the given studies";
			redirect( controller: "assay", action: "errorPage" );
			return;
		}
		
		// Create url to redirect to
		def format = params.get( "format", "sheets" )
		redirect( controller: "assay", action: "exportToExcel", params: [ "format": format, "ids": assayIds ] );
	}
}
