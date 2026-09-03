import { Button } from "@/components/ui/Button";

const JOURNEY = [
  { step: "Register", detail: "Create an account with an email and password." },
  { step: "Write your content", detail: "Profile, projects, skills, experience, education — once." },
  { step: "Choose a template", detail: "Pick from the catalogue. Nothing you wrote is duplicated." },
  { step: "Customize the theme", detail: "Adjust the settings that template actually supports." },
  { step: "Preview", detail: "See the real published output before it's public." },
  { step: "Publish", detail: "PortfolioHub builds and deploys it for you." },
  { step: "Share", detail: "A stable URL, yours to keep even if you change templates later." },
];

export default function LandingPage() {
  return (
    <div className="flex flex-col gap-24">
      <section className="grid gap-10 pt-8 md:grid-cols-[3fr_2fr] md:items-end">
        <div className="flex flex-col gap-6">
          <h1 className="text-3xl md:text-[var(--font-size-3xl)]">
            Write your portfolio once. Publish it through any template that fits.
          </h1>
          <p className="max-w-prose text-lg text-[var(--color-ink-muted)]">
            PortfolioHub keeps your projects, skills and experience separate from how they&apos;re
            presented. Pick a template, customize what it supports, and publish — then swap the
            template later without rewriting anything.
          </p>
          <div className="flex flex-wrap gap-3">
            <Button href="/register" variant="primary">
              Create your portfolio
            </Button>
            <Button href="/templates" variant="secondary">
              Explore templates
            </Button>
          </div>
        </div>
      </section>

      <section aria-labelledby="architecture-heading" className="grid gap-8 border-y border-[var(--color-line)] py-12 md:grid-cols-2">
        <div>
          <h2 id="architecture-heading" className="text-xl">
            What you write stays yours
          </h2>
          <ul className="mt-4 flex flex-col gap-2 text-[var(--color-ink-muted)]">
            <li>Profile and about</li>
            <li>Skills</li>
            <li>Projects</li>
            <li>Experience and education</li>
            <li>Achievements and social links</li>
          </ul>
        </div>
        <div>
          <h2 className="text-xl">What the template controls</h2>
          <ul className="mt-4 flex flex-col gap-2 text-[var(--color-ink-muted)]">
            <li>Layout and visual design</li>
            <li>Color and typography, where the template allows it</li>
            <li>Which sections are emphasized</li>
          </ul>
          <p className="mt-4 text-sm text-[var(--color-ink-faint)]">
            Switching templates changes this column only. Nothing on the left is duplicated or
            rewritten.
          </p>
        </div>
      </section>

      <section aria-labelledby="journey-heading">
        <h2 id="journey-heading" className="text-xl">
          From account to published portfolio
        </h2>
        <ol className="mt-6 flex flex-col gap-4">
          {JOURNEY.map((item, index) => (
            <li key={item.step} className="flex gap-4 border-b border-[var(--color-line)] pb-4">
              <span className="font-mono text-sm text-[var(--color-ink-faint)]">
                {String(index + 1).padStart(2, "0")}
              </span>
              <div>
                <p className="font-medium">{item.step}</p>
                <p className="text-sm text-[var(--color-ink-muted)]">{item.detail}</p>
              </div>
            </li>
          ))}
        </ol>
      </section>
    </div>
  );
}
