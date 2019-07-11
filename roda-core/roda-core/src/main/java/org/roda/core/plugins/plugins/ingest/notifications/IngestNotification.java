package org.roda.core.plugins.plugins.ingest.notifications;

import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.v2.jobs.Job;
import org.roda.core.data.v2.jobs.JobStats;
import org.roda.core.index.IndexService;
import org.roda.core.model.ModelService;

public interface IngestNotification {
  void notificate(ModelService model, IndexService index, Job job, JobStats jobStats)
    throws GenericException, RequestNotValidException, AuthorizationDeniedException;
}
