CREATE TABLE IF NOT EXISTS production_stage (
    id SERIAL PRIMARY KEY,
    stage_code VARCHAR(50) NOT NULL,
    stage_name VARCHAR(100) NOT NULL,
    display_order INTEGER NOT NULL,
    work_type VARCHAR(50) NOT NULL DEFAULT 'GENERIC',
    created_user_id INTEGER NOT NULL,
    CONSTRAINT uk_production_stage_user_code UNIQUE (created_user_id, stage_code),
    CONSTRAINT uk_production_stage_user_display_order UNIQUE (created_user_id, display_order)
)@@

CREATE TABLE IF NOT EXISTS production_stage_department_mapping (
    id SERIAL PRIMARY KEY,
    production_stage_id INTEGER,
    department_id INTEGER,
    created_user_id INTEGER
)@@

CREATE TABLE IF NOT EXISTS production_lot_allocation (
    id SERIAL PRIMARY KEY,
    production_lot_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',
    current_stage_id INTEGER,
    complete_date_in_english DATE,
    complete_date_in_nepali VARCHAR(10),
    CONSTRAINT uk_production_lot_allocation_product UNIQUE (production_lot_id, product_id)
)@@

CREATE TABLE IF NOT EXISTS production_lot_stage_entry (
    id SERIAL PRIMARY KEY,
    production_lot_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    production_stage_id INTEGER NOT NULL,
    department_id INTEGER,
    employee_id INTEGER,
    work_type VARCHAR(50) NOT NULL,
    expected_pieces INTEGER,
    completed_pieces INTEGER NOT NULL,
    extra_pieces INTEGER NOT NULL DEFAULT 0,
    damage_pieces INTEGER NOT NULL DEFAULT 0,
    add_to_master BOOLEAN NOT NULL DEFAULT FALSE,
    deduct_from_master BOOLEAN NOT NULL DEFAULT FALSE,
    remarks VARCHAR(500),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_user_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)@@

CREATE TABLE IF NOT EXISTS production_lot_stage_entry_employee (
    id SERIAL PRIMARY KEY,
    stage_entry_id INTEGER NOT NULL,
    department_id INTEGER,
    employee_id INTEGER NOT NULL,
    role_code VARCHAR(50),
    role_name VARCHAR(100),
    pieces INTEGER
)@@

CREATE TABLE IF NOT EXISTS production_lot_stage_size_breakdown (
    id SERIAL PRIMARY KEY,
    stage_entry_id INTEGER NOT NULL,
    size VARCHAR(30) NOT NULL,
    pieces INTEGER NOT NULL
)@@

ALTER TABLE production_stage_department_mapping
    ADD COLUMN IF NOT EXISTS production_stage_id INTEGER@@

ALTER TABLE production_stage_department_mapping
    ADD COLUMN IF NOT EXISTS department_id INTEGER@@

ALTER TABLE production_stage_department_mapping
    ADD COLUMN IF NOT EXISTS created_user_id INTEGER@@

ALTER TABLE production_stage
    DROP COLUMN IF EXISTS active@@

ALTER TABLE production_stage
    ADD COLUMN IF NOT EXISTS work_type VARCHAR(50) NOT NULL DEFAULT 'GENERIC'@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS production_lot_id INTEGER@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS product_id INTEGER@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS production_stage_id INTEGER@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS department_id INTEGER@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS employee_id INTEGER@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS work_type VARCHAR(50) NOT NULL DEFAULT 'GENERIC'@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS expected_pieces INTEGER@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS completed_pieces INTEGER@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS extra_pieces INTEGER NOT NULL DEFAULT 0@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS damage_pieces INTEGER NOT NULL DEFAULT 0@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS add_to_master BOOLEAN NOT NULL DEFAULT FALSE@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS deduct_from_master BOOLEAN NOT NULL DEFAULT FALSE@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS remarks VARCHAR(500)@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS completed BOOLEAN NOT NULL DEFAULT FALSE@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS created_user_id INTEGER@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP@@

ALTER TABLE production_lot_stage_entry
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP@@

ALTER TABLE production_lot_allocation
    ADD COLUMN IF NOT EXISTS status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS'@@

ALTER TABLE production_lot_allocation
    ADD COLUMN IF NOT EXISTS current_stage_id INTEGER@@

ALTER TABLE production_lot_allocation
    ADD COLUMN IF NOT EXISTS complete_date_in_english DATE@@

ALTER TABLE production_lot_allocation
    ADD COLUMN IF NOT EXISTS complete_date_in_nepali VARCHAR(10)@@

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'production_lot'
    ) THEN
        ALTER TABLE production_lot
            ALTER COLUMN current_stage SET DEFAULT 'Pending';
    END IF;
END $$@@

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'production_lot')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'product_department_rate') THEN
        UPDATE production_lot_allocation allocation
        SET current_stage_id = first_stage.stage_id
        FROM (
            SELECT DISTINCT ON (allocation_inner.id)
                   allocation_inner.id AS allocation_id,
                   stage.id AS stage_id
            FROM production_lot_allocation allocation_inner
            JOIN production_lot lot
              ON lot.id = allocation_inner.production_lot_id
            JOIN product_department_rate rate
              ON rate.product_id = allocation_inner.product_id
            JOIN production_stage_department_mapping mapping
              ON mapping.department_id = rate.department_id
            JOIN production_stage stage
              ON stage.id = mapping.production_stage_id
             AND stage.created_user_id = lot.created_user_id
            WHERE allocation_inner.current_stage_id IS NULL
            ORDER BY allocation_inner.id, stage.display_order, stage.id
        ) first_stage
        WHERE allocation.id = first_stage.allocation_id;

        UPDATE production_lot lot
        SET current_stage = first_stage.stage_name
        FROM (
            SELECT DISTINCT ON (lot_inner.id)
                   lot_inner.id AS production_lot_id,
                   stage.stage_name
            FROM production_lot lot_inner
            JOIN production_lot_allocation allocation
              ON allocation.production_lot_id = lot_inner.id
            JOIN product_department_rate rate
              ON rate.product_id = allocation.product_id
            JOIN production_stage_department_mapping mapping
              ON mapping.department_id = rate.department_id
            JOIN production_stage stage
              ON stage.id = mapping.production_stage_id
             AND stage.created_user_id = lot_inner.created_user_id
            WHERE lot_inner.current_stage = 'Cutting Master'
               OR lot_inner.current_stage IS NULL
            ORDER BY lot_inner.id, stage.display_order, stage.id
        ) first_stage
        WHERE lot.id = first_stage.production_lot_id;
    END IF;
END $$@@

ALTER TABLE production_lot_stage_entry_employee
    ADD COLUMN IF NOT EXISTS stage_entry_id INTEGER@@

ALTER TABLE production_lot_stage_entry_employee
    ADD COLUMN IF NOT EXISTS department_id INTEGER@@

ALTER TABLE production_lot_stage_entry_employee
    ADD COLUMN IF NOT EXISTS employee_id INTEGER@@

ALTER TABLE production_lot_stage_entry_employee
    ADD COLUMN IF NOT EXISTS role_code VARCHAR(50)@@

ALTER TABLE production_lot_stage_entry_employee
    ADD COLUMN IF NOT EXISTS role_name VARCHAR(100)@@

ALTER TABLE production_lot_stage_entry_employee
    ADD COLUMN IF NOT EXISTS pieces INTEGER@@

INSERT INTO production_lot_stage_entry_employee (
    stage_entry_id,
    department_id,
    employee_id,
    pieces
)
SELECT
    stage_entry.id,
    stage_entry.department_id,
    stage_entry.employee_id,
    stage_entry.completed_pieces
FROM production_lot_stage_entry stage_entry
WHERE stage_entry.department_id IS NOT NULL
  AND stage_entry.employee_id IS NOT NULL
  AND stage_entry.completed_pieces IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM production_lot_stage_entry_employee existing
      WHERE existing.stage_entry_id = stage_entry.id
        AND existing.employee_id = stage_entry.employee_id
  )@@

ALTER TABLE production_lot_stage_size_breakdown
    ADD COLUMN IF NOT EXISTS stage_entry_id INTEGER@@

ALTER TABLE production_lot_stage_size_breakdown
    ADD COLUMN IF NOT EXISTS size VARCHAR(30)@@

ALTER TABLE production_lot_stage_size_breakdown
    ADD COLUMN IF NOT EXISTS pieces INTEGER@@

UPDATE production_stage
SET work_type = 'GENERIC'
WHERE work_type IS NULL
   OR work_type IN ('GENERIC_ENTRY', 'DEPARTMENT_PIECE_ENTRY', 'COMPLETION')@@

UPDATE production_stage
SET work_type = 'STITCHING'
WHERE work_type = 'STITCHING_OUTPUT'@@

UPDATE production_lot_stage_entry
SET work_type = 'GENERIC'
WHERE work_type IS NULL
   OR work_type IN ('GENERIC_ENTRY', 'DEPARTMENT_PIECE_ENTRY', 'COMPLETION')@@

UPDATE production_lot_stage_entry
SET work_type = 'STITCHING'
WHERE work_type = 'STITCHING_OUTPUT'@@

ALTER TABLE production_lot_stage_entry
    DROP COLUMN IF EXISTS output_pieces@@

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'production_stage_department_mapping'
          AND column_name = 'stage'
    ) THEN
        INSERT INTO production_stage (stage_code, stage_name, display_order, created_user_id)
        SELECT DISTINCT
               stage,
               CASE stage
                   WHEN 'CUTTING' THEN 'Cutting'
                   WHEN 'STITCHING' THEN 'Stitching'
                   WHEN 'FINISHING_PACKING' THEN 'Finishing & Packing'
                   ELSE INITCAP(REPLACE(stage, '_', ' '))
               END,
               CASE stage
                   WHEN 'CUTTING' THEN 1
                   WHEN 'STITCHING' THEN 2
                   WHEN 'FINISHING_PACKING' THEN 3
                   ELSE 99
               END,
               created_user_id
        FROM production_stage_department_mapping
        WHERE stage IS NOT NULL
          AND created_user_id IS NOT NULL
        ON CONFLICT (created_user_id, stage_code) DO NOTHING;

        UPDATE production_stage_department_mapping mapping
        SET production_stage_id = stage.id
        FROM production_stage stage
        WHERE mapping.production_stage_id IS NULL
          AND mapping.stage IS NOT NULL
          AND mapping.created_user_id IS NOT NULL
          AND stage.stage_code = mapping.stage
          AND stage.created_user_id = mapping.created_user_id;
    END IF;
END $$@@

WITH ordered_stage_rows AS (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY created_user_id
               ORDER BY display_order NULLS LAST, id
           ) AS normalized_display_order
    FROM production_stage
)
UPDATE production_stage stage
SET display_order = ordered_stage_rows.normalized_display_order
FROM ordered_stage_rows
WHERE stage.id = ordered_stage_rows.id
  AND stage.display_order <> ordered_stage_rows.normalized_display_order@@

WITH duplicate_department_rows AS (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY production_stage_id, department_id
               ORDER BY id
           ) AS duplicate_rank
    FROM production_stage_department_mapping
    WHERE production_stage_id IS NOT NULL
      AND department_id IS NOT NULL
)
DELETE FROM production_stage_department_mapping mapping
USING duplicate_department_rows
WHERE mapping.id = duplicate_department_rows.id
  AND duplicate_department_rows.duplicate_rank > 1@@

ALTER TABLE production_stage
    DROP CONSTRAINT IF EXISTS uk_production_stage_user_display_order@@

ALTER TABLE production_stage
    ADD CONSTRAINT uk_production_stage_user_display_order UNIQUE (created_user_id, display_order)@@

ALTER TABLE production_stage_department_mapping
    DROP CONSTRAINT IF EXISTS uk_production_stage_role_code@@

ALTER TABLE production_stage_department_mapping
    DROP CONSTRAINT IF EXISTS uk_production_stage_mapping_display_order@@

ALTER TABLE production_stage_department_mapping
    DROP CONSTRAINT IF EXISTS uk_production_stage_department@@

ALTER TABLE production_stage_department_mapping
    ADD CONSTRAINT uk_production_stage_department UNIQUE (production_stage_id, department_id)@@

ALTER TABLE production_stage_department_mapping
    DROP COLUMN IF EXISTS display_order@@

ALTER TABLE production_stage_department_mapping
    DROP COLUMN IF EXISTS role_code@@

ALTER TABLE production_stage_department_mapping
    DROP COLUMN IF EXISTS role_name@@

ALTER TABLE production_stage_department_mapping
    DROP COLUMN IF EXISTS required@@

ALTER TABLE production_stage_department_mapping
    DROP COLUMN IF EXISTS stage@@

ALTER TABLE production_stage_department_mapping
    DROP COLUMN IF EXISTS stage_role@@

ALTER TABLE production_stage_department_mapping
    DROP COLUMN IF EXISTS role@@

DROP TABLE IF EXISTS production_stage_seed@@

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'production_lot_cutting_pattern_entry'
    ) THEN
        INSERT INTO production_lot_stage_entry (
            production_lot_id,
            product_id,
            production_stage_id,
            work_type,
            expected_pieces,
            completed_pieces,
            extra_pieces,
            damage_pieces,
            add_to_master,
            deduct_from_master,
            remarks,
            completed,
            created_user_id,
            created_at,
            updated_at
        )
        SELECT
            cutting.production_lot_id,
            cutting.product_id,
            cutting.production_stage_id,
            'CUTTING_PATTERN',
            NULL,
            cutting.total_pieces,
            0,
            0,
            FALSE,
            FALSE,
            cutting.remarks,
            cutting.completed,
            cutting.created_user_id,
            cutting.created_at,
            cutting.updated_at
        FROM production_lot_cutting_pattern_entry cutting
        WHERE cutting.production_lot_id IS NOT NULL
          AND cutting.product_id IS NOT NULL
          AND cutting.production_stage_id IS NOT NULL
          AND cutting.total_pieces IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM production_lot_stage_entry existing
              WHERE existing.production_lot_id = cutting.production_lot_id
                AND existing.product_id = cutting.product_id
                AND existing.production_stage_id = cutting.production_stage_id
                AND existing.department_id IS NULL
                AND existing.work_type = 'CUTTING_PATTERN'
                AND existing.created_user_id = cutting.created_user_id
          );
    END IF;
END $$@@

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'production_lot_cutting_pattern_entry'
    ) THEN
        INSERT INTO production_lot_stage_entry_employee (
            stage_entry_id,
            employee_id,
            role_code,
            role_name
        )
        SELECT
            stage_entry.id,
            cutting.cutting_master_employee_id,
            'CUTTING_MASTER',
            'Cutting Master'
        FROM production_lot_cutting_pattern_entry cutting
        JOIN production_lot_stage_entry stage_entry
          ON stage_entry.production_lot_id = cutting.production_lot_id
         AND stage_entry.product_id = cutting.product_id
         AND stage_entry.production_stage_id = cutting.production_stage_id
         AND stage_entry.department_id IS NULL
         AND stage_entry.work_type = 'CUTTING_PATTERN'
         AND stage_entry.created_user_id = cutting.created_user_id
        WHERE cutting.cutting_master_employee_id IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM production_lot_stage_entry_employee existing
              WHERE existing.stage_entry_id = stage_entry.id
                AND existing.employee_id = cutting.cutting_master_employee_id
                AND existing.role_code = 'CUTTING_MASTER'
          );

        INSERT INTO production_lot_stage_entry_employee (
            stage_entry_id,
            employee_id,
            role_code,
            role_name
        )
        SELECT
            stage_entry.id,
            cutting.pattern_master_employee_id,
            'PATTERN_MASTER',
            'Pattern Master'
        FROM production_lot_cutting_pattern_entry cutting
        JOIN production_lot_stage_entry stage_entry
          ON stage_entry.production_lot_id = cutting.production_lot_id
         AND stage_entry.product_id = cutting.product_id
         AND stage_entry.production_stage_id = cutting.production_stage_id
         AND stage_entry.department_id IS NULL
         AND stage_entry.work_type = 'CUTTING_PATTERN'
         AND stage_entry.created_user_id = cutting.created_user_id
        WHERE cutting.pattern_master_employee_id IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM production_lot_stage_entry_employee existing
              WHERE existing.stage_entry_id = stage_entry.id
                AND existing.employee_id = cutting.pattern_master_employee_id
                AND existing.role_code = 'PATTERN_MASTER'
          );
    END IF;
END $$@@

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'production_lot_cutting_size_breakdown'
    ) THEN
        INSERT INTO production_lot_stage_size_breakdown (
            stage_entry_id,
            size,
            pieces
        )
        SELECT
            stage_entry.id,
            breakdown.size,
            breakdown.pieces
        FROM production_lot_cutting_size_breakdown breakdown
        JOIN production_lot_cutting_pattern_entry cutting
          ON cutting.id = breakdown.cutting_pattern_entry_id
        JOIN production_lot_stage_entry stage_entry
          ON stage_entry.production_lot_id = cutting.production_lot_id
         AND stage_entry.product_id = cutting.product_id
         AND stage_entry.production_stage_id = cutting.production_stage_id
         AND stage_entry.department_id IS NULL
         AND stage_entry.work_type = 'CUTTING_PATTERN'
         AND stage_entry.created_user_id = cutting.created_user_id
        WHERE breakdown.size IS NOT NULL
          AND breakdown.pieces IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM production_lot_stage_size_breakdown existing
              WHERE existing.stage_entry_id = stage_entry.id
                AND existing.size = breakdown.size
                AND existing.pieces = breakdown.pieces
          );
    END IF;
END $$@@

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'production_lot_stitching_entry'
    ) THEN
        INSERT INTO production_lot_stage_entry (
            production_lot_id,
            product_id,
            production_stage_id,
            employee_id,
            work_type,
            expected_pieces,
            completed_pieces,
            extra_pieces,
            damage_pieces,
            add_to_master,
            deduct_from_master,
            remarks,
            completed,
            created_user_id,
            created_at,
            updated_at
        )
        SELECT
            stitching.production_lot_id,
            stitching.product_id,
            stitching.production_stage_id,
            stitching.stitching_employee_id,
            'STITCHING',
            stitching.expected_pieces,
            stitching.stitched_pieces,
            stitching.extra_pieces,
            stitching.damage_pieces,
            stitching.add_to_master,
            stitching.deduct_from_master,
            stitching.remarks,
            stitching.completed,
            stitching.created_user_id,
            stitching.created_at,
            stitching.updated_at
        FROM production_lot_stitching_entry stitching
        WHERE stitching.production_lot_id IS NOT NULL
          AND stitching.product_id IS NOT NULL
          AND stitching.production_stage_id IS NOT NULL
          AND stitching.stitched_pieces IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM production_lot_stage_entry existing
              WHERE existing.production_lot_id = stitching.production_lot_id
                AND existing.product_id = stitching.product_id
                AND existing.production_stage_id = stitching.production_stage_id
                AND existing.department_id IS NULL
                AND existing.work_type = 'STITCHING'
                AND existing.created_user_id = stitching.created_user_id
          );
    END IF;
END $$@@

DROP TABLE IF EXISTS production_lot_cutting_size_breakdown@@

DROP TABLE IF EXISTS production_lot_cutting_pattern_entry@@

DROP TABLE IF EXISTS production_lot_stitching_entry@@
