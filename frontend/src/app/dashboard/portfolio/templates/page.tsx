import { TemplateSelectionView } from "@/components/templates/TemplateSelectionView";

export default function ChangeTemplatePage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Template</h1>
      <TemplateSelectionView />
    </div>
  );
}
