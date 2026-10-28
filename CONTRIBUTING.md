# Contributing

Thanks for taking a look. This repo is a learning log, so the best contributions are the ones that make a lesson more *correct* or easier to understand.

## Good contributions

- **Corrections.** A wrong claim in a note, a misleading comment, or a bug in a demo. Please say what you ran and what you saw.
- **Clarity.** A better explanation, a missing edge case, a broken link or a typo.
- **Verification.** A test that checks a demo against an independent method (a brute force, or a different algorithm).

Large rewrites or new days are better discussed in an issue first.

## Conventions

- One lesson per day: `notes/dayNN-slug.md`, `src/dayNN/ClassName.java`, and a row in `PROGRESS.md`.
- Each demo is a single runnable `main` and must compile with a plain `javac` on JDK 17 or newer (Day 49 additionally needs JUnit and Mockito, see its notes).
- Prefer printing a result next to its expected value, so a run explains itself.
- Notes should only claim what the code demonstrates. If you state a number, run the code and confirm it.
- Commit messages follow `day NN: short description`.

## Checking your change

```bash
mkdir -p out && javac -sourcepath src -d out src/dayNN/*.java && java -cp out ClassName
```

Pull requests run the same compile check for every day through GitHub Actions.
