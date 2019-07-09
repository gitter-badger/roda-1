package org.roda.core.plugins.plugins.ingest.steps;

import org.roda.core.data.exceptions.JobException;

public interface IngestCommand {
  void execute(IngestExecutePack pack) throws JobException;
}
