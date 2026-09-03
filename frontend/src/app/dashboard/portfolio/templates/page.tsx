import { EmptyState } from "@/components/states/EmptyState";

// Template selection/switching lands in Phase 3, against the B3 template
// contract/registry backend milestone.
export default function ChangeTemplatePage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Template</h1>
      <EmptyState
        heading="No template selected"
        description="Once the template registry is connected, you'll choose and preview a template here without losing anything you've written."
      />
    </div>
  );
}
