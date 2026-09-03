import { EmptyState } from "@/components/states/EmptyState";

// Publish/status UI lands in Phase 5, against the B5 publishing backend
// milestone. Statuses shown here must use the backend's exact enum
// (DRAFT/QUEUED/BUILDING/DEPLOYING/PUBLISHED/FAILED/ROLLED_BACK per the
// docs) once that contract exists — not an invented one.
export default function DeploymentsPage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Deployments</h1>
      <EmptyState
        heading="No deployments yet"
        description="Build and publish history will appear here once publishing is connected."
      />
    </div>
  );
}
