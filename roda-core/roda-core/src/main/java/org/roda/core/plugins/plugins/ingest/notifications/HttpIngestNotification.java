package org.roda.core.plugins.plugins.ingest.notifications;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.roda.core.common.notifications.HTTPNotificationProcessor;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.v2.jobs.Job;
import org.roda.core.data.v2.jobs.JobStats;
import org.roda.core.data.v2.jobs.Report;
import org.roda.core.data.v2.notifications.Notification;
import org.roda.core.index.IndexService;
import org.roda.core.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpIngestNotification implements IngestNotification {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpIngestNotification.class);

  private String to;

  public HttpIngestNotification(String to) {
    this.to = to;
  }

  @Override
  public void notificate(ModelService model, IndexService index, Job job, JobStats jobStats)
    throws GenericException, AuthorizationDeniedException {
    if (StringUtils.isNotBlank(to)) {
      Notification notification = new Notification();
      String outcome = Report.PluginState.SUCCESS.toString();

      if (jobStats.getSourceObjectsProcessedWithFailure() > 0) {
        outcome = Report.PluginState.FAILURE.toString();
      }

      notification.setSubject("RODA ingest process finished - " + outcome);
      notification.setFromUser(this.getClass().getSimpleName());
      notification.setRecipientUsers(Collections.singletonList(to));
      Map<String, Object> scope = new HashMap<>();
      scope.put(HTTPNotificationProcessor.JOB_KEY, job);
      model.createNotification(notification, new HTTPNotificationProcessor(to, scope));
    }
  }
}
