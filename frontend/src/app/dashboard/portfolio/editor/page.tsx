import { PortfolioEditor } from "@/components/portfolio/PortfolioEditor";

export default function PortfolioEditorPage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Editor</h1>
      <PortfolioEditor />
    </div>
  );
}
