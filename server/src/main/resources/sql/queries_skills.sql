-- Query
-- GetAllSkillNodes
SELECT
  s.id,
  s.name,
  s.description,
  s.cost,
  s.max_level,
  s.x,
  s.y,
  se.type AS effect_type,
  se.stat,
  se.element_type,
  se.double_value,
  se.int_value,
  se.category
FROM skills s
LEFT JOIN skill_effects se ON se.skill_id = s.id
ORDER BY s.id;

-- Query
-- GetAllSkillPrerequisites
SELECT skill_id, prerequisite_id FROM skill_prerequisites;

-- Query
-- SaveSkillNode
INSERT INTO skills (id, name, description, cost, max_level, x, y)
VALUES (?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (id) DO NOTHING;

-- Query
-- SaveSkillEffect
INSERT INTO skill_effects (skill_id, type, stat, element_type, double_value, int_value, category)
VALUES (?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (skill_id) DO NOTHING;

-- Query
-- SaveSkillPrerequisite
INSERT INTO skill_prerequisites (skill_id, prerequisite_id)
VALUES (?, ?)
ON CONFLICT DO NOTHING;
