package com.ericsson.eiffel.remrem.semantics;


import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.ACTIVITY_CANCELED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.ACTIVITY_FINISHED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.ACTIVITY_STARTED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.ACTIVITY_TRIGGERED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.ANNOUNCEMENT_PUBLISHED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.ARTIFACT_CREATED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.ARTIFACT_PUBLISHED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.COMPOSITION_DEFINED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.CONFIDENCELEVEL_MODIFIED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.CONFIGURATION_APPLIED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.ENVIRONMENT_DEFINED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.FLOWCONTEXT_DEFINED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.SOURCECHANGE_CREATED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.SOURCECHANGE_SUBMITTED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.TESTCASE_FINISHED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.TESTCASE_STARTED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.TESTSUITE_FINISHED;
import static com.ericsson.eiffel.remrem.semantics.EiffelEventType.TESTSUITE_STARTED;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.ericsson.eiffel.remrem.protocol.ValidationResult;
import com.ericsson.eiffel.remrem.semantics.factory.EiffelOutputValidatorFactory;
import com.ericsson.eiffel.remrem.semantics.validator.EiffelValidationException;
import com.ericsson.eiffel.remrem.semantics.validator.EiffelValidator;
import com.ericsson.eiffel.semantics.events.EiffelActivityCanceledEvent;
import com.ericsson.eiffel.semantics.events.EiffelActivityFinishedEvent;
import com.ericsson.eiffel.semantics.events.EiffelActivityStartedEvent;
import com.ericsson.eiffel.semantics.events.EiffelActivityTriggeredEvent;
import com.ericsson.eiffel.semantics.events.EiffelAnnouncementPublishedEvent;
import com.ericsson.eiffel.semantics.events.EiffelArtifactCreatedEvent;
import com.ericsson.eiffel.semantics.events.EiffelArtifactPublishedEvent;
import com.ericsson.eiffel.semantics.events.EiffelCompositionDefinedEvent;
import com.ericsson.eiffel.semantics.events.EiffelConfidenceLevelModifiedEvent;
import com.ericsson.eiffel.semantics.events.EiffelConfigurationAppliedEvent;
import com.ericsson.eiffel.semantics.events.EiffelEnvironmentDefinedEvent;
import com.ericsson.eiffel.semantics.events.EiffelFlowContextDefinedEvent;
import com.ericsson.eiffel.semantics.events.EiffelSourceChangeCreatedEvent;
import com.ericsson.eiffel.semantics.events.EiffelSourceChangeSubmittedEvent;
import com.ericsson.eiffel.semantics.events.EiffelTestCaseFinishedEvent;
import com.ericsson.eiffel.semantics.events.EiffelTestCaseStartedEvent;
import com.ericsson.eiffel.semantics.events.EiffelTestSuiteFinishedEvent;
import com.ericsson.eiffel.semantics.events.EiffelTestSuiteStartedEvent;
import com.ericsson.eiffel.semantics.events.Event;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Named("eiffel-semantics")
public class SemanticsService implements MsgService{

    private static final String EVENT_PARAMS = "eventParams";
    private static final String MSG_PARAMS = "msgParams";
    private static final String MESSAGE = "message";
    private static final String CAUSE = "cause";
    private static final String EIFFELSEMANTICS = "eiffelsemantics";
    private static final String ID = "id";
    private static final String META = "meta";

    public static final Logger log = LoggerFactory.getLogger(SemanticsService.class);

    private static Gson gson = new Gson();
    private static Map<EiffelEventType, Class<? extends Event>> eventTypes=SemanticsService.eventType();
    
    public static Map<EiffelEventType, Class<? extends Event>> eventType()
    {
        eventTypes = new HashMap<>();
        eventTypes.put(ARTIFACT_PUBLISHED, EiffelArtifactPublishedEvent.class);
        eventTypes.put(ACTIVITY_FINISHED, EiffelActivityFinishedEvent.class);
        eventTypes.put(ARTIFACT_CREATED, EiffelArtifactCreatedEvent.class);
        eventTypes.put(ACTIVITY_CANCELED, EiffelActivityCanceledEvent.class);
        eventTypes.put(ACTIVITY_STARTED, EiffelActivityStartedEvent.class);
        eventTypes.put(ACTIVITY_TRIGGERED, EiffelActivityTriggeredEvent.class);
        eventTypes.put(CONFIDENCELEVEL_MODIFIED, EiffelConfidenceLevelModifiedEvent.class);
        eventTypes.put(ANNOUNCEMENT_PUBLISHED, EiffelAnnouncementPublishedEvent.class);
        eventTypes.put(COMPOSITION_DEFINED, EiffelCompositionDefinedEvent.class);
        eventTypes.put(CONFIGURATION_APPLIED, EiffelConfigurationAppliedEvent.class);
        eventTypes.put(ENVIRONMENT_DEFINED, EiffelEnvironmentDefinedEvent.class);
        eventTypes.put(FLOWCONTEXT_DEFINED, EiffelFlowContextDefinedEvent.class);
        eventTypes.put(SOURCECHANGE_CREATED, EiffelSourceChangeCreatedEvent.class);
        eventTypes.put(SOURCECHANGE_SUBMITTED, EiffelSourceChangeSubmittedEvent.class);
        eventTypes.put(TESTCASE_FINISHED, EiffelTestCaseFinishedEvent.class);
        eventTypes.put(TESTCASE_STARTED, EiffelTestCaseStartedEvent.class);
        eventTypes.put(TESTSUITE_FINISHED, EiffelTestSuiteFinishedEvent.class);
        eventTypes.put(TESTSUITE_STARTED, EiffelTestSuiteStartedEvent.class);
       return eventTypes; 
    }
    @Override
    public String generateMsg(String msgType, JsonObject bodyJson){
        EiffelEventType eiffelType = EiffelEventType.fromString(msgType);
        if (eiffelType == null) {
            log.error("Unknown message type requested: " + msgType);
            return createErrorResponse("Unknown message type requested", "'" + msgType + "' is not in the vocabulary of this service");
        }
        Class<? extends Event> eventType = eventTypes.get(eiffelType);

        JsonObject msgNodes = bodyJson.get(MSG_PARAMS).getAsJsonObject();
        JsonObject eventNodes = bodyJson.get(EVENT_PARAMS).getAsJsonObject();

        Event event = eventCreation(msgType, eventType, msgNodes, eventNodes);

       String result = gson.toJson(event);
        try {
            outputValidate(eiffelType, result);
        } catch (EiffelValidationException e) {
            log.error("Could not validate message. Reason:" +  e.getMessage() +"\nCause: " + e.getCause().toString());
            return createErrorResponse(e.getMessage(), e.getCause().toString());
        }
        return result;
    }
    private static Event eventCreation(String msgType, Class<? extends Event> eventType, JsonObject msgNodes,
            JsonObject eventNodes) {
        Event event = createEvent(eventNodes, eventType);
        event.generateMeta(msgType, msgNodes);
        event.setMeta(event.meta);
        return event;
    }
    
    private static Event createEvent(JsonObject eventNodes, Class<? extends Event> eventType) {
        return gson.fromJson(eventNodes, eventType);
    }

    private String createErrorResponse(final String message, final String cause){
        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty(MESSAGE, message);
        errorResponse.addProperty(CAUSE, cause.replace("\n", ""));
        return errorResponse.toString();
    }
    
    private void outputValidate(EiffelEventType eiffelType, String jsonStringInput) throws EiffelValidationException {
        EiffelValidator validator = EiffelOutputValidatorFactory.getEiffelValidator(eiffelType);
        JsonObject jsonObject = new JsonParser().parse(jsonStringInput).getAsJsonObject();
        validator.validate(jsonObject);
    }

    @Override
    public String getEventId(JsonObject json) {
        if (json.isJsonObject() && json.getAsJsonObject().has(META) && json.getAsJsonObject()
                .getAsJsonObject(META).has(ID)) {
            return json.getAsJsonObject().getAsJsonObject(META)
                    .get(ID).getAsString();
        }
        return null;
    }

    @Override
    public String getFamily(JsonObject eiffelMessage) {
        return null;
    }
    
    @Override
    public String getType(JsonObject eiffelMessage) {
        return null;
    }
    @Override
    public String getServiceName() {
        return EIFFELSEMANTICS;
    }

    @Override
    public ValidationResult validateMsg(String msgType, JsonObject jsonvalidateMessage) {
        ValidationResult validationResult = null;
        EiffelEventType eiffelType = EiffelEventType.fromString(msgType);
        String result = gson.toJson(jsonvalidateMessage);
        try {
            outputValidate(eiffelType, result);
            validationResult = new ValidationResult(true, "");
        } catch (EiffelValidationException e) {
            validationResult = new ValidationResult(false, e.getLocalizedMessage());
        }
        return validationResult;
    }
}
